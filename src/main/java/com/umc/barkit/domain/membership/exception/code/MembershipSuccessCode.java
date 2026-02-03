package com.umc.barkit.domain.membership.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MembershipSuccessCode implements BaseSuccessCode {
    // ========== 대표 멤버십 설정 성공 ==========
    MEMBERSHIP2007(HttpStatus.OK, "MEMBERSHIP2007", "대표 멤버십으로 설정되었습니다."),
    MEMBERSHIP2008(HttpStatus.OK, "MEMBERSHIP2008", "대표 멤버십이 해제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
