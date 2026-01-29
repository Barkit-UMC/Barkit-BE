package com.umc.barkit.domain.user.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UserRequestDto {
    public record EmailCheckRequestDto(
            @NotBlank
            String email
    ){
    }

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

    public record TermAgreement(
            @NotNull Long termId,
            @NotNull boolean isAgreed
    ){}
}
