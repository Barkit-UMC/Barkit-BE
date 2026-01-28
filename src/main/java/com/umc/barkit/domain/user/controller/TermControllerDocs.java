package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.res.TermResponseDto;
import com.umc.barkit.domain.user.dto.res.TermResponseDto.TermListResponse;
import com.umc.barkit.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface TermControllerDocs {

    @Operation(
            summary = "약관 목록 조회 API",
            description = "3개의 약관 데이터를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    ApiResponse<TermListResponse> getTerms();
}
