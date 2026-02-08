package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.res.UserResponseDto.LoginResponseDto;
import com.umc.barkit.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
}
