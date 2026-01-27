package com.umc.barkit.domain.favorite.controller;

import com.umc.barkit.domain.favorite.dto.request.FavoriteCreateRequest;
import com.umc.barkit.domain.favorite.dto.response.FavoriteResponse;
import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import com.umc.barkit.domain.favorite.service.FavoriteService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 조회 (최대 5개)
    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites(
            @RequestHeader("Authorization") String token
    ) {
        Long userId = 1L; // TODO: JWT에서 추출

        List<FavoriteStoreBrand> favorites = favoriteService.getFavorites(userId);
        List<FavoriteResponse> result = favorites.stream()
                .map(FavoriteResponse::from)
                .toList();

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    // 즐겨찾기 추가
    @PostMapping
    public ApiResponse<Void> createFavorite(
            @RequestHeader("Authorization") String token,
            @RequestBody FavoriteCreateRequest request
    ) {
        Long userId = 1L; // TODO: JWT에서 추출

        favoriteService.createFavorite(userId, request.getStoreBrandId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/{favoriteId}")
    public ApiResponse<Void> deleteFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long favoriteId
    ) {
        Long userId = 1L; // TODO: JWT에서 추출

        favoriteService.deleteFavorite(userId, favoriteId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }
}
