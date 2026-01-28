package com.umc.barkit.domain.store.controller;

import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;
import com.umc.barkit.domain.store.exception.code.StoreSuccessCode;
import com.umc.barkit.domain.store.service.query.StoreQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController implements StoreControllerDocs{

    private final StoreQueryService storeQueryService;

    @Override
    public ApiResponse<List<StoreResDTO.SearchedStore>> search(String query, DistanceType distanceType, Category category, Double userLat, Double userLng, Double centerLat, Double centerLng, Sort sort) {
        StoreSuccessCode code = StoreSuccessCode.FOUND;
        return ApiResponse.onSuccess(
                code,
                storeQueryService.search(query, distanceType, category, userLat, userLng, centerLat, centerLng, sort)
        );
    }

    @Override
    public ApiResponse<StoreResDTO.StoreDetail> detail(String placeId, Double userLat, Double userLng) {
        StoreSuccessCode code = StoreSuccessCode.FOUND;
        return ApiResponse.onSuccess(
                code,
                storeQueryService.detail(placeId, userLat, userLng)
        );
    }
}
