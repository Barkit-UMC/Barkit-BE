package com.umc.barkit.domain.favorite.controller;

import com.umc.barkit.domain.favorite.dto.request.FavoriteCreateRequest;
import com.umc.barkit.domain.favorite.dto.response.FavoriteResponse;
import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import com.umc.barkit.domain.favorite.service.FavoriteService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favorite", description = "즐겨찾기 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "즐겨찾기 목록 조회",
            description = "로그인 사용자가 즐겨찾기한 매장 목록을 최대 5개까지 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites(
            @RequestHeader("Authorization") String token
    ) {
        Long userId = 100L; // TODO: JWT에서 추출

        List<FavoriteStoreBrand> favorites = favoriteService.getFavorites(userId);
        List<FavoriteResponse> result = favorites.stream()
                .map(FavoriteResponse::from)
                .toList();

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @Operation(
            summary = "즐겨찾기 추가",
            description = "특정 매장을 즐겨찾기에 추가합니다. 최대 5개까지 등록 가능하며, 중복 등록은 제한됩니다."
    )
    @PostMapping
    public ApiResponse<Void> createFavorite(
            @RequestHeader("Authorization") String token,
            @RequestBody FavoriteCreateRequest request
    ) {
        Long userId = 100L; // TODO: JWT에서 추출

        favoriteService.createFavorite(userId, request.getStoreBrandId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    @Operation(
            summary = "즐겨찾기 삭제",
            description = "즐겨찾기한 매장을 삭제합니다. 실제 DB 삭제가 아닌 Soft Delete 방식으로 처리됩니다."
    )
    @DeleteMapping("/{favoriteId}")
    public ApiResponse<Void> deleteFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long favoriteId
    ) {
        Long userId = 100L; // TODO: JWT에서 추출

        favoriteService.deleteFavorite(userId, favoriteId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }
}
