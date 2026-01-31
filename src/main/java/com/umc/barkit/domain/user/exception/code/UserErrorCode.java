package com.umc.barkit.domain.user.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    INVALID(HttpStatus.UNAUTHORIZED, "AUTH4001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH4004", "이미 사용 중인 아이디입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4003", "비밀번호 확인이 일치하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4005", "사용자를 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
