package com.umc.barkit.domain.user.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 토큰 응답 매핑 DTO
 * @param accessToken
 * @param refreshToken
 */
public record KakaoTokenResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_token_expires_in") Long refreshTokenExpiresIn,
        String scope
) {
}
