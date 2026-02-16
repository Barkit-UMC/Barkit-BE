package com.umc.barkit.domain.store.controller;

import com.umc.barkit.domain.store.dto.req.StoreReqDTO;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.exception.StoreException;
import com.umc.barkit.domain.store.exception.code.StoreErrorCode;
import com.umc.barkit.domain.store.exception.code.StoreSuccessCode;
import com.umc.barkit.domain.store.service.query.StoreQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StoreController implements StoreControllerDocs{

    private final StoreQueryService storeQueryService;

    @Override
    public ApiResponse<StoreResDTO.SearchedStoreSlice> search(
            CustomUserDetails userDetails,
            StoreReqDTO.SearchReq req,
            DistanceType distanceType,
            Category category,
            int cursor,
            int size
    ) {

        if (userDetails == null) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        if (distanceType == DistanceType.CENTER && (req.centerLat() == null || req.centerLng() == null)) {
            throw new StoreException(StoreErrorCode.CENTER_LOCATION_REQUIRED);
        }

        StoreSuccessCode code = StoreSuccessCode.FOUND;

        return ApiResponse.onSuccess(
                code,
                storeQueryService.search(req, distanceType, category, cursor, size)
        );
    }

    @Override
    public ApiResponse<StoreResDTO.StoreDetail> detail(
            CustomUserDetails userDetails,
            StoreReqDTO.DetailReq req
    ) {
        Long userId = userDetails.getUserId();
        StoreSuccessCode code = StoreSuccessCode.FOUND;
        return ApiResponse.onSuccess(
                code,
                storeQueryService.detail(req, userId)
        );
    }
}
