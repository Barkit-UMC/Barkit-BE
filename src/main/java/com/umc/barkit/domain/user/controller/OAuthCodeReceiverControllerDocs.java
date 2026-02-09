package com.umc.barkit.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestParam;

public interface OAuthCodeReceiverControllerDocs {
    @Operation(
            summary = "카카오 OAuth 인가 코드 수신 (백엔드 로컬 테스트용)",
            description = "출력된 code를 복사하여 Swagger/Postman에서 소셜 연동 API 호출에 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    String kakao(
            @Parameter(description = "카카오 인가 코드", required = true)
            @RequestParam String code
    );

    @Operation(
            summary = "네이버 OAuth 인가 코드 수신 (백엔드 로컬 테스트용)",
            description = "출력된 code/state를 복사하여 Swagger/Postman에서 소셜 연동 API 호출에 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    String naver(
            @Parameter(description = "네이버 인가 코드", required = true)
            @RequestParam String code,
            @Parameter(description = "네이버 state", required = true)
            @RequestParam String state
    );
}
