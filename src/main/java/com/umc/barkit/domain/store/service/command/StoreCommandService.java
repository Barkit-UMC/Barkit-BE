package com.umc.barkit.domain.store.service.command;

import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;

import java.util.List;

public interface StoreCommandService {
    List<StoreResDTO.SearchedStore> saveAndMap(
            StoreBrand storeBrand,
            List<GoogleResDTO.Place> places,
            Double userLat,
            Double userLng,
            Double sendLat,
            Double sendLng,
            boolean applyDistanceFilter,
            List<StoreResDTO.SearchedStoreMembership> membershipsDTO
    );
}
