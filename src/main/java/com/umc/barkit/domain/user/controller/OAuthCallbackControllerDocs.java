package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.res.UserResponseDto.LoginResponseDto;
import com.umc.barkit.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestParam;

public interface OAuthCallbackControllerDocs {

    @Operation(
            summary = "카카오 로그인 콜백 API (백엔드 로컬 테스트용)",
            description = "카카오 Redirect URI로 들어오는 code(인가 코드)를 받아 로그인 로직을 실행합니다.<br>" +
                    "로컬에서 백엔드 단독으로 카카오 로그인 흐름을 검증할 때 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공, 서비스 JWT(accessToken/refreshToken) 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "code 누락/형식 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "카카오 인증 실패/토큰 발급 실패")
    })
    ApiResponse<LoginResponseDto> kakaoCallback(
            @RequestParam String code
    );

    @Operation(
            summary = "네이버 로그인 콜백 API (백엔드 로컬 테스트용)",
            description = "네이버 Redirect URI로 들어오는 code(인가 코드)와 state를 받아 로그인 로직을 실행합니다.<br>" +
                    "로컬에서 백엔드 단독으로 네이버 로그인 흐름을 검증할 때 사용합니다.<br>" +
                    "state는 CSRF 방지를 위해 네이버 인가 요청 시 생성된 값이며, 콜백에서 반드시 함께 전달됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공, 서비스 JWT(accessToken/refreshToken) 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "code/state 누락 또는 형식 오류")
    })
    ApiResponse<LoginResponseDto> naverCallback(
            @Parameter(description = "네이버 인가 코드", required = true)
            @RequestParam String code,
            @Parameter(description = "네이버 state (CSRF 방지용)", required = true)
            @RequestParam String state
    );

    @Operation(
            summary = "네이버 authorize URL 생성 API (백엔드 로컬 테스트용)",
            description = "백엔드 로컬 테스트를 위해 네이버 로그인 인가(Authorize) URL을 생성합니다.<br>" +
                    "생성된 URL로 접속하면 네이버 로그인/동의 후 /oauth/naver/callback 으로 redirect 됩니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "authorize URL 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "redirectUri 검증 실패(허용 목록 불일치)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "authorize URL 생성 실패")
    })
    ApiResponse<String> naverAuthorizeUrl();
}
