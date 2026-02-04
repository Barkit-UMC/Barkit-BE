package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.store.dto.req.StoreReqDTO;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;

public interface StoreQueryService {
    StoreResDTO.StoreDetail detail (String placeId, Double userLat, Double userLng,Long userId);

    StoreResDTO.SearchedStoreSlice search(
            StoreReqDTO.SearchReq req,
            DistanceType distanceType,
            Category category,
            Sort sort,
            int cursor,
            int size
    );
}
