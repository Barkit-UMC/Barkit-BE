package com.umc.barkit.domain.user.oauth.dto;

public record NaverUserResponseDto(
        String resultcode,
        String message,
        NaverResponse response
) {
    public record NaverResponse(
            String id,
            String name,
            String email
    ) {
    }
}
