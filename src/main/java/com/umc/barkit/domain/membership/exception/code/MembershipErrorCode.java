package com.umc.barkit.domain.membership.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MembershipErrorCode implements BaseErrorCode {
    // ========== 멤버십 브랜드 에러 (BRAND) ==========
    BRAND4001(HttpStatus.NOT_FOUND, "BRAND4001", "존재하지 않는 브랜드입니다."),
    BRAND4002(HttpStatus.CONFLICT, "BRAND4002", "이미 존재하는 브랜드입니다."),

    // ========== 멤버십 카드 에러 (MEMBERSHIP) ==========
    MEMBERSHIP4001(HttpStatus.BAD_REQUEST, "MEMBERSHIP4001", "멤버십 번호를 입력해주세요."),
    MEMBERSHIP4002(HttpStatus.BAD_REQUEST, "MEMBERSHIP4002", "멤버십 번호는 최대 20자까지 입력 가능합니다."),
    MEMBERSHIP4003(HttpStatus.BAD_REQUEST, "MEMBERSHIP4003", "멤버십 번호는 숫자만 입력 가능합니다."),
    MEMBERSHIP4004(HttpStatus.NOT_FOUND,"MEMBERSHIP4004","등록된 사용자 멤버십이 존재하지 않습니다."),
    MEMBERSHIP4006(HttpStatus.FORBIDDEN, "MEMBERSHIP4006", "본인의 멤버십만 대표 멤버십으로 설정할 수 있습니다."),
    MEMBERSHIP4007(HttpStatus.BAD_REQUEST, "MEMBERSHIP4007", "대표 멤버십은 최대 3개까지 설정할 수 있습니다."),
    MEMBERSHIP4008(HttpStatus.BAD_REQUEST, "MEMBERSHIP4008", "멤버십 번호는 최소 12자 이상이어야 합니다."),
    MEMBERSHIP4009(HttpStatus.CONFLICT, "MEMBERSHIP4009", "이미 저장된 멤버십 브랜드입니다."),

    // ========== 바코드 에러 (BARCODE) ==========
    BARCODE4001(HttpStatus.BAD_REQUEST, "BARCODE4001", "지원하지 않는 바코드 형식입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
