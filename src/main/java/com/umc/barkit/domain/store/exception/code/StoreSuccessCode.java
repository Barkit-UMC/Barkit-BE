package com.umc.barkit.domain.store.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreSuccessCode implements BaseSuccessCode {
    FOUND(HttpStatus.OK,
            "STORE2001",
            "성공적으로 매장을 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
