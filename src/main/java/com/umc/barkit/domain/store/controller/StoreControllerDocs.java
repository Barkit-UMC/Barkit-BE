package com.umc.barkit.domain.store.controller;

import com.umc.barkit.domain.store.dto.req.StoreReqDTO;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;
import com.umc.barkit.global.annotation.ValidCursor;
import com.umc.barkit.global.annotation.ValidSize;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/map")
@Validated
public interface StoreControllerDocs {

    // 지도 검색 기능(멤버십/매장)
    @Operation(
            summary = "지도 검색 기능 API",
            description = "지도에서 멤버십/매장 기준으로 검색합니다. " +
                    "카테고리와 정렬기준(현재 내 위치/지도 중심 위치, 거리순/인기순)을 선택할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    @GetMapping("/search")
    ApiResponse<StoreResDTO.SearchedStoreSlice> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute StoreReqDTO.SearchReq req,
            @RequestParam(value = "distanceType", defaultValue = "CURRENT") DistanceType distanceType,
            @RequestParam(value = "category", defaultValue = "ALL") Category category,
            @RequestParam(value = "sort", defaultValue = "DISTANCE") Sort sort,
            @RequestParam(value = "cursor", defaultValue = "0") @ValidCursor int cursor,
            @RequestParam(value = "size", defaultValue = "20") @ValidSize int size
            );


    // 매장 상세 정보 조회
    @Operation(
            summary = "매장 상세 정보 조회 API",
            description="매장에서 적용 가능한 멤버십과 매장 상세 정보(영업시간, 전화번호 등)을 확인할 수 있습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })

    @GetMapping("/store")
    ApiResponse<StoreResDTO.StoreDetail> detail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String googleId,
            @RequestParam("userLat") Double userLat,
            @RequestParam("userLng") Double userLng
    );

}
