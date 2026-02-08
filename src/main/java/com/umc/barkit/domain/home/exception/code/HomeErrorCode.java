package com.umc.barkit.domain.home.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HomeErrorCode implements BaseErrorCode {

    // ========== 홈 인기 매장 에러 (HOME) ==========
    HOME4001(
            HttpStatus.NOT_FOUND,
            "HOME4001",
            "존재하지 않는 멤버십입니다."
    ),

    HOME5001(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "HOME5001",
            "홈 인기 매장 조회 중 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    HomeErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
