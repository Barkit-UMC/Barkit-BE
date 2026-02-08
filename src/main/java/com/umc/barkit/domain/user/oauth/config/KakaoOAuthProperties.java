package com.umc.barkit.domain.user.oauth.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.oauth")
/**
 * application.yml의 kakao.oauth.* 설정을 자바 객체로 바인딩
 */
public record KakaoOAuthProperties(
        String restApiKey,
        String clientSecret,
        String tokenUri,
        String userInfoUri,
        List<String> redirectUriAllowlist
) {
}
