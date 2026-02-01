package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.req.UserRequestDto.EmailCheckRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.exception.code.UserSuccessCode;
import com.umc.barkit.domain.user.service.command.UserCommandService;
import com.umc.barkit.domain.user.service.query.UserQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController implements UserControllerDocs{

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    // 아이디(이메일) 중복 확인 API
    @PostMapping("/auth/check-email")
    public ApiResponse<UserResponseDto.EmailCheckResponseDto> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailDto){
        UserResponseDto.EmailCheckResponseDto response = userQueryService.checkEmailAvailability(emailDto.email());
        return ApiResponse.onSuccess(UserSuccessCode.EMAIL_CHECK_OK, response);
    }

    // 회원가입 API
    @PostMapping("/auth/signup")
    public ApiResponse<UserResponseDto.SignupResponseDto> signup(@RequestBody @Valid UserRequestDto.SignupRequestDto signupRequestDto) {
        UserResponseDto.SignupResponseDto response = userCommandService.signup(signupRequestDto);
        return ApiResponse.onSuccess(UserSuccessCode.CREATED, response);
    }

    // 로그인 API
    @PostMapping("/auth/login")
    public ApiResponse<UserResponseDto.LoginResponseDto> login(
            @RequestBody @Valid UserRequestDto.LoginRequestDto loginDto
    ){
        return ApiResponse.onSuccess(UserSuccessCode.LOGIN_OK, userQueryService.login(loginDto));
    }

    // 개인정보 조회 API
    @GetMapping("/users/me")
    public ApiResponse<UserResponseDto.PersonalInfoResponseDto> getPersonalInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserResponseDto.PersonalInfoResponseDto response = userQueryService.getPersonalInfo(userId);
        return ApiResponse.onSuccess(UserSuccessCode.PERSONAL_INFO_OK, response);
    }


    // 생년월일 변경 API
    @PatchMapping("/users/me/birth-date")
    public ApiResponse<UserResponseDto.PersonalInfoResponseDto> updateBirthDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto.UpdateBirthDateRequestDto birthDateDto
    ){
        Long userId = userDetails.getUserId();
        userCommandService.updateBirthDate(userId, birthDateDto.birthDate());
        UserResponseDto.PersonalInfoResponseDto response = userQueryService.getPersonalInfo(userId);
        return ApiResponse.onSuccess(UserSuccessCode.BIRTH_DATE_UPDATED, response);
    }



}
