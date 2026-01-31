package com.umc.barkit.domain.user.dto.res;

import lombok.Builder;

public class UserResponseDto {

    // 이메일 중복확인
    public record EmailCheckResponseDto(
            boolean isAvailable,
            String message
    ){}

    // 회원가입
    public record SignupResponseDto(
            Long userId,
            String email
    ){}

    // 로그인
    @Builder
    public record LoginResponseDto(
            Long userId,
            String accessToken
    ){}
}
