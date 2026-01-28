package com.umc.barkit.domain.store.controller;

import com.umc.barkit.domain.store.dto.req.StoreReqDTO;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;
import com.umc.barkit.domain.store.exception.StoreException;
import com.umc.barkit.domain.store.exception.code.StoreErrorCode;
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
    public ApiResponse<StoreResDTO.SearchedStoreSlice> search(
            StoreReqDTO.SearchReq req,
            DistanceType distanceType,
            Category category,
            Sort sort,
            int cursor,
            int size
    ) {
        if (distanceType == DistanceType.CENTER && (req.centerLat() == null || req.centerLng() == null)) {
            throw new StoreException(StoreErrorCode.CENTER_LOCATION_REQUIRED);
        }

        StoreSuccessCode code = StoreSuccessCode.FOUND;

        return ApiResponse.onSuccess(
                code,
                storeQueryService.search(req, distanceType, category, sort, cursor, size)
        );

    }
}
