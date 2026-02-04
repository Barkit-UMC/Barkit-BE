package com.umc.barkit.domain.membership.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserMembershipBrandResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        private List<UserBrandDTO> brands;
        private Long nextCursor;
        private Boolean hasNext;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBrandDTO {
        private Long userMembershipBrandId;
        private String name;
        private String logoUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterMembershipResultDTO {
        private Long userMembershipBrandId;
        private String membershipNumber;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserMembershipBarcodeDTO {
        private String membershipNumber;
        private String logoUrl;
        private String brandName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableStoreListDTO {
        private List<AvailableStoreDTO> stores;
        private Boolean hasNext;
        private Long nextCursor;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableStoreDTO {
        private Long storeId;
        private String storeName;
        private String brandName;
        private String logoUrl;
        private String address; // 지금은 null
    }

}