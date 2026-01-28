package com.umc.barkit.domain.store.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StoreResDTO {

    @Builder
    public record SearchedStore(
            String name,
            StoreResDTO.SearchedStoreLocation location, //매장 위도, 경도
            String address,
            String phone,
            List<StoreResDTO.SearchedStoreMembership> memberships,
            Double distanceKm,
            String directionUrl, //길찾기(네이버/카카오) url
            String photoUrl
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

    @Getter
    @Builder
    public static class StoreDetail {
        private String name;
        private Double distance;
        private StoreLocation location;
        private StoreContact contact;
        private StoreHourInfo hourInfo;
        private List<MembershipInfo> membership;
        private List<StorePhotoInfo> photos;
    }


    @Builder
    public record StoreLocation(
            double lat,
            double lng
    ){}
    @Builder
    public record StoreContact(
            String address,
            String phoneNumber,
            String homepage
    ){}

    @Builder
    public record StoreHourInfo(
            List<String> weekdayText,
            Boolean isOpen
    ){}

    @Builder
    public record MembershipInfo(
            String name,
            String logoUrl
    ){}

    @Builder
    public record StorePhotoInfo(
            String url,
            Integer width,
            Integer height
    ){}

}
