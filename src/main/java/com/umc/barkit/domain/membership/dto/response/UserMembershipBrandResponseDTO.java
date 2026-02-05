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
    public static class MembershipDetailDTO {
        private Long userMembershipBrandId;
        private String membershipBrandName;
        private String themeColor;
        private String logoUrl;
        private String membershipNumber;
        private List<StoreBrandDTO> storeBrands;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreBrandDTO {
        private Long storeBrandId;
        private String name;
        private String logoUrl;
    }
}