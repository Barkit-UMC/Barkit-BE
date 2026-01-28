package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.res.TermResponseDto;
import com.umc.barkit.domain.user.dto.res.TermResponseDto.TermListResponse;
import com.umc.barkit.domain.user.exception.code.TermSuccessCode;
import com.umc.barkit.domain.user.service.query.TermQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TermController {

    private final TermQueryService termQueryService;

    // 약관 목록 조회 API
    @Operation(
            summary = "약관 목록 조회 API",
            description = "3개의 약관 데이터를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    @GetMapping("/api/terms")
    public ApiResponse<TermResponseDto.TermListResponse> getTerms(){
        TermListResponse terms = termQueryService.getTerms();
        return ApiResponse.onSuccess(TermSuccessCode.TERM_LIST_OK, terms);

    }

}
