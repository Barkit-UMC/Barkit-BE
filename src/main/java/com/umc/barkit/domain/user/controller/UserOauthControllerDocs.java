package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto.OAuthStatusResponseDto;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserOauthControllerDocs {
    @Operation(
            summary = "소셜 연동 상태 조회 API",
            description = "현재 로그인한 사용자의 소셜 연동 상태(카카오/네이버) 및 연결 정보(providerEmail, connectedAt)를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ApiResponse<OAuthStatusResponseDto> getOauthStatus(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "카카오 연동하기 API",
            description = "현재 로그인한 사용자 계정에 카카오 계정을 연결합니다. " +
                    "프론트에서 카카오 authorize를 통해 발급받은 code와 redirectUri를 전달해야 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "연동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "연동 실패")
    })
    ApiResponse<Void> connectKakao(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserRequestDto.KakaoLoginRequestDto request
    );

    @Operation(
            summary = "네이버 연동하기 API",
            description = "현재 로그인한 사용자 계정에 네이버 계정을 연결합니다. " +
                    "프론트에서 네이버 authorize를 통해 발급받은 code/state와 redirectUri를 전달해야 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "연동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "연동 실패")
    })
    ApiResponse<Void> connectNaver(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserRequestDto.NaverLoginRequestDto request
    );
}
