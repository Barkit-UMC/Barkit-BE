package com.umc.barkit.domain.store.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {
    // ========== STORE 에러 ==========
    STORE4001(HttpStatus.NOT_FOUND, "STORE4001", "존재하지 않는 매장입니다."),
    STORE4002(HttpStatus.BAD_REQUEST, "STORE4002", "유효하지 않은 매장 ID입니다."),
    STORE4003(HttpStatus.BAD_REQUEST, "STORE4003", "위도/경도 값이 올바르지 않습니다."),
    STORE4004(HttpStatus.CONFLICT, "STORE4004", "이미 동일한 위도/경도의 매장이 존재합니다."),

    // ========== BUSINESS HOUR 에러 ==========
    HOUR4001(HttpStatus.NOT_FOUND, "HOUR4001", "해당 매장의 영업시간 정보가 존재하지 않습니다."),
    HOUR4002(HttpStatus.BAD_REQUEST, "HOUR4002", "요일 형식이 올바르지 않습니다."),
    HOUR4003(HttpStatus.BAD_REQUEST, "HOUR4003", "영업 시작/종료 시간이 올바르지 않습니다."),
    HOUR4004(HttpStatus.CONFLICT, "HOUR4004", "이미 해당 요일의 영업시간이 존재합니다."),
    HOUR4005(HttpStatus.BAD_REQUEST, "HOUR4005", "자정이 넘는 영업시간은 종료 시간이 시작 시간보다 앞서야 합니다."),

    // ========== FACILITY 에러 ==========
    FACILITY4001(HttpStatus.NOT_FOUND, "FACILITY4001", "존재하지 않는 편의시설입니다."),
    FACILITY4002(HttpStatus.BAD_REQUEST, "FACILITY4002", "유효하지 않은 편의시설 ID입니다."),

    // ========== STORE FACILITY 에러 ==========
    SF4001(HttpStatus.BAD_REQUEST, "SF4001", "해당 매장에 등록할 수 없는 편의시설입니다."),
    SF4002(HttpStatus.CONFLICT, "SF4002", "이미 매장에 등록된 편의시설입니다."),

    // ========== STORE MEMBERSHIP 에러 ==========
    SM4001(HttpStatus.NOT_FOUND, "SM4001", "해당 매장의 멤버십 정보가 존재하지 않습니다."),
    SM4002(HttpStatus.CONFLICT, "SM4002", "이미 매장에 등록된 멤버십입니다."),
    SM4003(HttpStatus.BAD_REQUEST, "SM4003", "등록할 수 없는 멤버십 브랜드입니다."),

    // ========== STORE_BRAND 에러 ==========
    SBRAND4001(HttpStatus.NOT_FOUND, "SBRAND4001", "존재하지 않는 매장 브랜드입니다."),
    SBRAND4002(HttpStatus.BAD_REQUEST, "SBRAND4002", "유효하지 않은 매장 브랜드 ID입니다."),
    SBRAND4003(HttpStatus.CONFLICT, "SBRAND4003", "이미 존재하는 매장 브랜드입니다."),
    SBRAND4004(HttpStatus.BAD_REQUEST, "SBRAND4004", "브랜드 이름 형식이 올바르지 않습니다."),

    // ========== MAP 에러 ==========
    MAP4004(HttpStatus.BAD_REQUEST, "MAP4004", "사용자 위치 정보가 올바르지 않습니다."),
    MAP5002(HttpStatus.BAD_GATEWAY, "MAP5002", "지도 서비스와 통신 중 오류가 발생하였습니다."),
    MAP5003(HttpStatus.INTERNAL_SERVER_ERROR, "MAP5003", "지도 서비스 응답 처리 중 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
