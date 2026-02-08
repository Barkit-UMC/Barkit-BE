package com.umc.barkit.domain.user.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverTokenResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn,
        String error,
        @JsonProperty("error_description") String errorDescription
) {
}
