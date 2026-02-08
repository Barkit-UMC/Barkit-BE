package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto.LoginResponseDto;
import com.umc.barkit.domain.user.exception.code.UserSuccessCode;
import com.umc.barkit.domain.user.service.query.UserQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
/**
 * (테스트 전용) 소셜 로그인 Redirect URI로 들어오는 콜백을 받아,
 * code(인가 코드)를 이용해 실제 로그인 로직을 실행해보기 위한 컨트롤러
 */
public class OAuthCallbackController implements OAuthCallbackControllerDocs{

    private final UserQueryService userQueryService;

    // 프론트 연동 전에 "백엔드만"으로도 카카오 로그인 흐름이 정상 동작하는지 확인
    @GetMapping("/oauth/kakao/callback")
    public ApiResponse<LoginResponseDto> kakaoCallback(@RequestParam String code) {
        var dto = new UserRequestDto.KakaoLoginRequestDto(code, "http://localhost:8080/oauth/kakao/callback");
        return ApiResponse.onSuccess(UserSuccessCode.LOGIN_OK, userQueryService.kakaoLogin(dto));
    }

    // 프론트 연동 전에 "백엔드만"으로도 네이버 로그인 흐름이 정상 동작하는지 확인
    @GetMapping("/oauth/naver/callback")
    public ApiResponse<LoginResponseDto> naverCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        var dto = new UserRequestDto.NaverLoginRequestDto(
                code,
                state,
                "http://localhost:8080/oauth/naver/callback"
        );
        return ApiResponse.onSuccess(UserSuccessCode.LOGIN_OK, userQueryService.naverLogin(dto));
    }

    @GetMapping("/oauth/naver/authorize-url")
    public ApiResponse<String> naverAuthorizeUrl() {
        String url = userQueryService.getNaverAuthorizeUrl("http://localhost:8080/oauth/naver/callback");
        return ApiResponse.onSuccess(UserSuccessCode.LOGIN_OK, url);
    }
}
