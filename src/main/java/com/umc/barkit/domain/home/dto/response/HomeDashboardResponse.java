package com.umc.barkit.domain.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 홈 대시보드 전용 응답 DTO
 * - 홈 화면 전용
 * - 멤버십 카드 리스트 제공
 * - 매장 즐겨찾기 기능 제거됨 (기획 변경)
 */
public class HomeDashboardResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardDTO {

        /**
         * 사용자 보유 멤버십 카드 요약
         */
        private List<MembershipSummaryDTO> memberships;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembershipSummaryDTO {

        /**
         * 멤버십 브랜드 ID
         */
        private Long membershipBrandId;

        /**
         * 멤버십 브랜드 이름
         */
        private String name;

        /**
         * 멤버십 브랜드 로고 URL
         */
        private String logoUrl;
    }
}
