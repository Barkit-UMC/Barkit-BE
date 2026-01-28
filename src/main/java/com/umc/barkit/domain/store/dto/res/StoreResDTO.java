package com.umc.barkit.domain.store.dto.res;

import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import lombok.Builder;

import java.util.List;

public class StoreResDTO {

    @Builder
    public record SearchedStoreSlice(
            List<SearchedStore> content,
            boolean hasNext,
            int nextCursor
    ) {}

    @Builder
    public record SearchedStore(
            Long storeId,
            String googleId,
            GoogleResDTO.DisplayName name,
            StoreResDTO.SearchedStoreLocation location, //매장 위도, 경도
            String address,
            String phone,
            List<StoreResDTO.SearchedStoreMembership> memberships,
            Double distanceKm,
            String directionUrl //길찾기(네이버/카카오) url
    ){}

    @Builder
    public record SearchedStoreLocation(
            Double lat,
            Double lng
    ) {}

    @Builder
    public record SearchedStoreMembership(
            Long id,
            String name,
            String logoUrl
    )
    {}
}
