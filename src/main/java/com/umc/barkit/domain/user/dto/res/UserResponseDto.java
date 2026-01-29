package com.umc.barkit.domain.user.dto.res;

public class UserResponseDto {

    public record EmailCheckResponseDto(
            boolean isAvailable,
            String message
    ){}

    public record SignupResponseDto(
            Long userId,
            String email
    ){}
}
