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
}