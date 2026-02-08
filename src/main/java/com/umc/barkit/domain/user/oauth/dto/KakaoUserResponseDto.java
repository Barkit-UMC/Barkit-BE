package com.umc.barkit.domain.user.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 유저 정보 응답 매핑 DTO
 * @param id - 카카오 고유 사용자 식별자
 * @param kakaoAccount - 이메일
 * @param properties - 닉네임
 */
public record KakaoUserResponseDto(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount,
        KakaoProperties properties
) {
    public record KakaoAccount(
            @JsonProperty("email") String email
    ) {}

    public record KakaoProperties(
            @JsonProperty("nickname") String nickname
    ) {}
}
