package com.umc.barkit.domain.membership.controller;

import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.service.MembershipBrandQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership-brands")
public class MembershipBrandController {

    private final MembershipBrandQueryService membershipBrandQueryService;

    // GET /api/membership-brands/top4
    @Operation(
            summary = "상위 4개 인기 멤버십 브랜드 조회",
            description = "등록 수가 많은 상위 4개 멤버십 브랜드를 조회합니다. 브랜드 ID, 이름, 로고 URL을 포함합니다."
    )
    @GetMapping("/top4")
    public ResponseEntity<ApiResponse<MembershipBrandResponseDTO.Top4BrandsDTO>> getTop4MembershipBrands() {
        MembershipBrandResponseDTO.Top4BrandsDTO result =
                membershipBrandQueryService.getTop4MembershipBrands();

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }

    // GET /api/membership-brands/popular
    @Operation(
            summary = "인기 멤버십 브랜드 10개 조회",
            description = "등록 수가 많은 상위 10개 멤버십 브랜드를 조회합니다. 브랜드 ID와 이름만 포함합니다."
    )
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<MembershipBrandResponseDTO.PopularBrandsDTO>> getPopularMembershipBrands() {
        MembershipBrandResponseDTO.PopularBrandsDTO result =
                membershipBrandQueryService.getPopularMembershipBrands();

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }

    @Operation(
            summary = "멤버십 브랜드 검색",
            description = "키워드로 멤버십 브랜드를 검색합니다. " +
                    "대소문자 구분 없이 부분 일치로 검색되며, " +
                    "커서 기반 페이지네이션을 지원합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<MembershipBrandResponseDTO.SearchResultDTO>> searchMembershipBrands(
            @Parameter(description = "검색 키워드 (대소문자 무시, 부분 일치)", required = true)
            @RequestParam String keyword,

            @Parameter(description = "커서 (이전 응답의 nextCursor 값)", required = false, example = "0")
            @RequestParam(required = false) Long cursor,

            @Parameter(description = "한 번에 가져올 개수 (기본값: 20)", required = false, example = "20")
            @RequestParam(required = false) Integer limit
    ) {
        MembershipBrandResponseDTO.SearchResultDTO result =
                membershipBrandQueryService.searchMembershipBrands(keyword, cursor, limit);

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }
}