package com.umc.barkit.domain.user.oauth.client;

import com.umc.barkit.domain.user.oauth.config.KakaoOAuthProperties;
import com.umc.barkit.domain.user.oauth.dto.KakaoTokenResponseDto;
import com.umc.barkit.domain.user.oauth.dto.KakaoUserResponseDto;
import lombok.RequiredArgsConstructor;
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
// 카카오 OAuth 서버와 통신하는 코드
public class KakaoApiClient {

    private final KakaoOAuthProperties props;
    private final RestTemplate restTemplate;

    public KakaoApiClient(KakaoOAuthProperties props, RestTemplateBuilder builder) {
        this.props = props;
        this.restTemplate = builder.build();
    }

    /**
     * 인가 코드(code) + redirectUri로 카카오 토큰 발급 API 호출
     * POST https://kauth.kakao.com/oauth/token
     */
    public KakaoTokenResponseDto exchangeToken(String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", props.restApiKey());
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        if (props.clientSecret() != null && !props.clientSecret().isBlank()) {
            body.add("client_secret", props.clientSecret());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<KakaoTokenResponseDto> response =
                restTemplate.postForEntity(props.tokenUri(), request, KakaoTokenResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Kakao token exchange failed");
        }
        return response.getBody();
    }

    /**
     * accessToken으로 카카오 유저정보 조회 API 호출
     * GET https://kapi.kakao.com/v2/user/me
     */
    public KakaoUserResponseDto getUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserResponseDto> response =
                restTemplate.exchange(props.userInfoUri(), HttpMethod.GET, request, KakaoUserResponseDto.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Kakao user info failed");
        }
        return response.getBody();
    }
}
