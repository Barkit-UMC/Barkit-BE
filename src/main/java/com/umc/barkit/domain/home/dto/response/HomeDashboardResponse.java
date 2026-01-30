package com.umc.barkit.domain.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 홈 대시보드 전용 응답 DTO
 * - 홈 화면 전용
 * - 다른 API와 공유 ❌
 */
public class HomeDashboardResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardDTO {

        /**
         * 즐겨찾기 매장 요약 (최대 5개)
         */
        private List<FavoriteStoreDTO> favoriteStores;

        /**
         * 사용자 보유 멤버십 요약
         * - ACTIVE만
         * - 현재는 최근 등록순 기준
         */
        private List<MembershipSummaryDTO> memberships;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteStoreDTO {

        private Long favoriteId;
        private Long storeId;
        private String storeName;

        /**
         * 사용자 기준:
         * 내가 가진 멤버십 중 하나라도
         * 이 매장에서 혜택 가능하면 true
         */
        private Boolean isBenefitAvailable;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembershipSummaryDTO {

        private Long membershipBrandId;
        private String name;
        private String logoUrl;

        /**
         * TODO
         * - 멤버십 사용 이력 테이블 없음
         * - 1차 구현: 최근 등록순
         */
    }
}
