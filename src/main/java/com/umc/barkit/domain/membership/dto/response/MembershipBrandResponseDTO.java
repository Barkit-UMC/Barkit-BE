package com.umc.barkit.domain.membership.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MembershipBrandResponseDTO {

    // 기본 10개 멤버십 브랜드 조회 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaultBrandsDTO {
        private List<BrandSimpleDTO> brands;
    }

    // 멤버십 브랜드 간단 정보 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandSimpleDTO {
        private Long membershipBrandId;
        private String name;
        private String logoUrl;
    }

    // 인기 멤버십 브랜드 10개 조회 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularBrandsDTO {
        private List<BrandNameOnlyDTO> brands;
    }

    // 멤버십 브랜드 이름만 있는 DTO (ID, 이름만)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandNameOnlyDTO {
        private Long membershipBrandId;
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        private List<BrandSimpleDTO> brands;
        private Long nextCursor;
        private Boolean hasNext;
    }
}