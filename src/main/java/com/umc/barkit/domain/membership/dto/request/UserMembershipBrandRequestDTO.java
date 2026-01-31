package com.umc.barkit.domain.membership.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserMembershipBrandRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterMembershipDTO {
        private String membershipNumber;   // 멤버십 번호
        private String barcodeRawValue;    // 바코드 문자열 (프론트에서 추출)
    }
}