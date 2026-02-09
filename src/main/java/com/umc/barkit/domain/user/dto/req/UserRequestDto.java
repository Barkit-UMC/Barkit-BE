package com.umc.barkit.domain.user.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;

public class UserRequestDto {

    // 이메일 중복확인
    public record EmailCheckRequestDto(
            @NotBlank
            String email
    ){
    }

    // 회원가입
    public record SignupRequestDto(
            @NotBlank
            String name,
            @Email @NotBlank
            String email,
            @NotBlank
            String password,
            @NotBlank
            String confirmPassword,
            @NotNull
            @PastOrPresent
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @Schema(example = "2002-01-01")
            LocalDate birthDate,

            @NotEmpty
            @Valid
            List<TermAgreement> terms
    ){}

    // 약관 동의
    public record TermAgreement(
            @NotNull Long termId,
            @NotNull boolean isAgreed
    ){}

    // 로그인
    public record LoginRequestDto(
            @NotBlank
            String email,
            @NotBlank
            String password
    ){}

    // 생년월일 변경
    public record UpdateBirthDateRequestDto(
            @NotNull
            @PastOrPresent
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @Schema(example = "2003-05-19")
            LocalDate birthDate
    ) {
    }

    // 비밀번호 변경
    public record UpdatePasswordRequestDto(
            @NotNull
            String currentPassword,

            @NotNull
            String newPassword,

            @NotNull
            String confirmPassword
    ) {}

    // 엑세스 토큰 재발급
    public record RefreshRequestDto(
            @NotBlank
            String refreshToken
    ) {}

    // 카카오 로그인
    public record KakaoLoginRequestDto(
            @NotBlank String code,
            @NotBlank String redirectUri
    ) {}

    // 네이버 로그인
    public record NaverLoginRequestDto(
            @NotBlank String code,
            @NotBlank String state,
            @NotBlank String redirectUri
    ) {
    }

    // 알림 설정 변경
    public record UpdateNotificationRequestDto(
            Boolean enabled
    ) {}

    // 위치 권한 동의 변경
    public record UpdateLocationConsentRequestDto(
            Boolean consented

    ) {}
}
