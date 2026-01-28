package com.umc.barkit.domain.user.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {

    EMAIL_CHECK_OK(HttpStatus.OK, "AUTH2000", "아이디 중복 확인에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "AUTH2001", "회원가입이 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
