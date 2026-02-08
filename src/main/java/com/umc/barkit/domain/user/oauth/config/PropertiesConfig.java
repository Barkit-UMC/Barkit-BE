package com.umc.barkit.domain.user.oauth.config;

import com.umc.barkit.domain.user.oauth.config.KakaoOAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        KakaoOAuthProperties.class,
        NaverOauthProperties.class
})
// KakaoOAuthProperties를 스프링 빈으로 등록
public class PropertiesConfig {
}
