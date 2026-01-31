package com.umc.barkit.domain.user.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
}
