package com.umc.barkit.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    // ===== Common =====
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON4004", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),

    // ===== Valid =====
    VALID_FAIL(HttpStatus.BAD_REQUEST, "VALID4001", "검증에 실패했습니다."),

    // ===== Kakao API =====
    KAKAO_NO_RESULT(HttpStatus.NOT_FOUND, "KAKAO4001", "카카오 API 검색 결과가 없습니다."),
    KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY, "KAKAO5001", "카카오 API 호출 중 오류가 발생했습니다."),

    // ===== Google API =====
    GOOGLE_NO_RESULT(HttpStatus.NOT_FOUND, "GOOGLE4001", "구글 플레이스 상세 정보를 찾을 수 없습니다."),
    GOOGLE_API_ERROR(HttpStatus.BAD_GATEWAY, "GOOGLE5001", "구글 API 호출 중 오류가 발생했습니다."),

    // ========== 멤버십 브랜드 에러 (BRAND) ==========
    BRAND4001(HttpStatus.NOT_FOUND, "BRAND4001", "존재하지 않는 브랜드입니다."),

    // ========== 멤버십 카드 에러 (MEMBERSHIP) ==========
    MEMBERSHIP4001(HttpStatus.BAD_REQUEST, "MEMBERSHIP4001", "멤버십 번호를 입력해주세요."),
    MEMBERSHIP4002(HttpStatus.BAD_REQUEST, "MEMBERSHIP4002", "멤버십 번호는 최대 20자까지 입력 가능합니다."),
    MEMBERSHIP4003(HttpStatus.BAD_REQUEST, "MEMBERSHIP4003", "멤버십 번호는 숫자만 입력 가능합니다."),
    MEMBERSHIP4004(HttpStatus.BAD_REQUEST, "MEMBERSHIP4004", "멤버십 번호는 최소 12자 이상이어야 합니다."),
    MEMBERSHIP4005(HttpStatus.CONFLICT, "MEMBERSHIP4005", "이미 저장된 멤버십 브랜드입니다."),

    // ========== 바코드 에러 (BARCODE) ==========
    BARCODE4001(HttpStatus.BAD_REQUEST, "BARCODE4001", "지원하지 않는 바코드 형식입니다."),

    // ========== 홈 에러 (HOME) ==========
    HOME4001(HttpStatus.INTERNAL_SERVER_ERROR, "HOME4001", "홈 데이터를 조회하는 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
