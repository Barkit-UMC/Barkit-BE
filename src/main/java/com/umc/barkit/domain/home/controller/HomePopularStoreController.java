package com.umc.barkit.domain.home.controller;

import com.umc.barkit.domain.home.dto.response.HomePopularStoreResponse;
import com.umc.barkit.domain.home.exception.code.HomeSuccessCode;
import com.umc.barkit.domain.home.service.HomePopularStoreService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomePopularStoreController {

    private final HomePopularStoreService homePopularStoreService;

    @Operation(
            summary = "홈 인기 매장 미리보기 조회",
            description = "사용자가 보유한 특정 멤버십 기준으로 적립/할인 가능한 매장 중 " +
                    "조회수(viewCount)가 높은 상위 매장들을 미리보기 형태로 제공합니다. " +
                    "해당 API는 홈 화면 노출용이며, 최대 5개의 인기 매장을 반환합니다."
    )
    @GetMapping("/memberships/{userMembershipBrandId}/popular-stores")
    public ApiResponse<HomePopularStoreResponse> getPopularStores(
            @PathVariable Long userMembershipBrandId
    ) {
        return ApiResponse.onSuccess(
                HomeSuccessCode.HOME_POPULAR_STORE_PREVIEW_SUCCESS,
                homePopularStoreService.getPopularStores(userMembershipBrandId)
        );
    }
}