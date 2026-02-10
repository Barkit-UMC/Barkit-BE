package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.exception.code.UserSuccessCode;
import com.umc.barkit.domain.user.service.command.UserOauthCommandService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/oauth")
public class UserOauthController implements UserOauthControllerDocs{

    private final UserOauthCommandService userOauthCommandService;

    // 소셜 연동 상태 조회
    @GetMapping
    public ApiResponse<UserResponseDto.OAuthStatusResponseDto> getOauthStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        var response = userOauthCommandService.getStatus(userDetails.getUserId());
        return ApiResponse.onSuccess(UserSuccessCode.OAUTH_STATUS_OK, response);
    }

    // 카카오 연동하기
    @PostMapping("/kakao/connect")
    public ApiResponse<Void> connectKakao(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserRequestDto.KakaoLoginRequestDto request
    ) {
        userOauthCommandService.connectKakao(userDetails.getUserId(), request);
        return ApiResponse.onSuccess(UserSuccessCode.OAUTH_CONNECTED_OK, null);
    }

    // 네이버 연동하기
    @PostMapping("/naver/connect")
    public ApiResponse<Void> connectNaver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserRequestDto.NaverLoginRequestDto request
    ) {
        userOauthCommandService.connectNaver(userDetails.getUserId(), request);
        return ApiResponse.onSuccess(UserSuccessCode.OAUTH_CONNECTED_OK, null);
    }
}
