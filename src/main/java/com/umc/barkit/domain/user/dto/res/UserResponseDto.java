package com.umc.barkit.domain.user.dto.res;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            String accessToken,
            String refreshToken
    ){}


    // 엑세스 토큰 응답
    public record RefreshResponseDto(
            String accessToken
    ){}

    // 개인정보
    public record PersonalInfoResponseDto(
            String name,
            String email,
            String phoneNumber,
            LocalDate birthDate
    ) {}

    // 회원 탈퇴
    public record WithdrawResponseDto(
            Long userId,
            LocalDateTime deletedAt
    ) {}
}
