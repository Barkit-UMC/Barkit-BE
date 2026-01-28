package com.umc.barkit.domain.user.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermSuccessCode implements BaseSuccessCode {

    TERM_LIST_OK(HttpStatus.OK,
            "TERM2000",
            "약관 목록 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
