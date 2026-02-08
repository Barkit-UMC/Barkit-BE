package com.umc.barkit.domain.user.oauth.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver.oauth")
public record NaverOauthProperties(
        String clientId,
        String clientSecret,
        String stateSecret,
        String tokenUri,
        String userInfoUri,
        List<String> redirectUriAllowlist
) {
}
