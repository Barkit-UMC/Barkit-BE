package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;

import java.util.List;

public interface StoreQueryService {
    List<StoreResDTO.SearchedStore> search (String query, DistanceType distanceType, Category category, Double userLat, Double userLng, Double centerLat, Double centerLng, Sort sort);
}
