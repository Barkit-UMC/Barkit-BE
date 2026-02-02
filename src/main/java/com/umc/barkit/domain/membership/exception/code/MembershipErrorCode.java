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
    // ========== 바코드 에러 (BARCODE) ==========
    BARCODE4001(HttpStatus.BAD_REQUEST, "BARCODE4001", "지원하지 않는 바코드 형식입니다."),

    // ========== 사진 에러 (PHOTO) ==========
    PHOTO4001(HttpStatus.BAD_REQUEST, "PHOTO4001", "사진을 업로드해주세요."),
    PHOTO4002(HttpStatus.BAD_REQUEST, "PHOTO4002", "사진 크기는 최대 10MB까지 가능합니다."),
    PHOTO4003(HttpStatus.BAD_REQUEST, "PHOTO4003", "jpg, png 형식만 업로드 가능합니다."),
    PHOTO4004(HttpStatus.BAD_REQUEST, "PHOTO4004", "이미지 파일이 손상되었거나 읽을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;


}
