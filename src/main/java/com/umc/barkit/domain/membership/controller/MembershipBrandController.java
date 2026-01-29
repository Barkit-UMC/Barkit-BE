package com.umc.barkit.domain.membership.controller;

import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.service.MembershipBrandQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}