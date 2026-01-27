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
        private StoreFacilityInfo facilities;
    }


    @Getter
    @Builder
    public static class StoreContact {
        private String address;
        private String phoneNumber;
        private String homepage;
    }

    @Getter
    @Builder
    public static class MembershipInfo {
        private String name;
        private String logoUrl;
    }


    @Getter
    @Builder
    public static class StoreFacilityInfo {
        private Boolean wheelchair;
        private Boolean pet;
    }


    @Getter
    @Builder
    public static class StoreHourInfo {
        private String open;
        private String close;
        private Boolean isOpen;
    }


    @Getter
    @Builder
    public static class StoreLocation {
        private double lat;
        private double lng;
    }


    @Getter
    @Builder
    public static class StorePhotoInfo {
        private String url;
        private Integer width;
        private Integer height;
    }




    public record KakaoSearchResponse(
            List<KakaoDocument> documents
    ){}

    public record KakaoDocument(
            String place_name,
            String address_name,
            String phone,
            String x,
            String y
    ){}

    public record GooglePlaceDetailResponse(
            String id,
            DisplayName displayName,
            String formattedAddress,
            Location location,
            String websiteUri,
            String nationalPhoneNumber,
            Boolean wheelchairAccessibleEntrance,
            Boolean allowsDogs,
            RegularOpeningHours regularOpeningHours,

            List<GooglePhoto> photos
    ) {}

    public record DisplayName(
            String text
    ) {}

    public record Location(
            double latitude,
            double longitude
    ) {}

    public record RegularOpeningHours(
            List<String> weekdayDescriptions
    ) {}

    public record GooglePhoto(
            String name
    ) {}


}
