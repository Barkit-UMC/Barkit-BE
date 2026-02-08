package com.umc.barkit.domain.home.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HomeSuccessCode implements BaseSuccessCode {

    HOME_POPULAR_STORE_PREVIEW_SUCCESS(
            HttpStatus.OK,
            "HOME2001",
            "홈 인기 매장 미리보기 조회에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    HomeSuccessCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.httpStatus;
    }
}
