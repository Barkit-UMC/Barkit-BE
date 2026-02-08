package com.umc.barkit.domain.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 홈 대시보드 전용 응답 DTO
 */
public class HomeDashboardResponse {

    /**
     * 홈 대시보드 응답
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardDTO {

        /**
         * 대표 멤버십 카드 (isMain = true)
         */
        private List<MainMembershipDTO> mainMemberships;

        /**
         * 전체 멤버십 카드 (대표 멤버십 포함)
         */
        private List<MembershipSummaryDTO> memberships;
    }

    /**
     * 대표 멤버십 DTO (바코드 렌더링용 membershipNumber 포함)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainMembershipDTO {

        private Long userMembershipBrandId;
        private Long membershipBrandId;
        private String name;
        private String logoUrl;

        /**
         * 바코드 raw value (= membershipNumber)
         */
        private String membershipNumber;
    }

    /**
     * 일반 멤버십 DTO (바코드 없음)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembershipSummaryDTO {

        private Long userMembershipBrandId;
        private Long membershipBrandId;
        private String name;
        private String logoUrl;
    }
}
