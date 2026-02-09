package com.umc.barkit.domain.user.oauth.client;

import com.umc.barkit.domain.user.oauth.config.NaverOauthProperties;
import com.umc.barkit.domain.user.oauth.dto.NaverTokenResponseDto;
import com.umc.barkit.domain.user.oauth.dto.NaverUserResponseDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class NaverApiClient {
    private final NaverOauthProperties props;
    private final RestTemplate restTemplate;

    public NaverApiClient(NaverOauthProperties props, RestTemplateBuilder builder) {
        this.props = props;
        this.restTemplate = builder.build();
    }

    /**
     * 인가 코드(code) + redirectUri로 네이버 토큰 발급 API 호출
     * POST https://nid.naver.com/oauth2.0/token
     */
    public NaverTokenResponseDto exchangeToken(String code, String state, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", props.clientId());
        body.add("client_secret", props.clientSecret());
        body.add("code", code);
        body.add("state", state);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<NaverTokenResponseDto> response =
                restTemplate.postForEntity(props.tokenUri(), request, NaverTokenResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Naver token exchange failed");
        }
        if (response.getBody().accessToken() == null) {
            throw new IllegalStateException("Naver token exchange failed: " + response.getBody().error());
        }
        return response.getBody();
    }

    /**
     * accessToken으로 네이버 유저정보 조회 API 호출
     * GET https://openapi.naver.com/v1/nid/me
     */
    public NaverUserResponseDto getUserInfo(String naverAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(naverAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<NaverUserResponseDto> response =
                restTemplate.exchange(props.userInfoUri(), HttpMethod.GET, request, NaverUserResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Naver user info failed");
        }
        return response.getBody();
    }

    public String buildAuthorizeUrl(String redirectUri, String state) {
        String r = java.net.URLEncoder.encode(redirectUri, java.nio.charset.StandardCharsets.UTF_8);
        return "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + props.clientId()
                + "&redirect_uri=" + r
                + "&state=" + state;
    }
}
