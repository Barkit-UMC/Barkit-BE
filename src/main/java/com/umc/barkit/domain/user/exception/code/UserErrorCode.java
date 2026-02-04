package com.umc.barkit.domain.user.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    // 로그인
    INVALID(HttpStatus.UNAUTHORIZED, "AUTH4001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH4002", "이미 사용 중인 아이디입니다."),

    // 리프레쉬 토큰
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH4003", "리프레시 토큰이 필요합니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH4004", "리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH4005", "리프레시 토큰이 만료되었습니다."),

    // 비밀번호
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4001", "비밀번호는 8~12자, 영문+특수문자 조합이어야 합니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER4002", "비밀번호 확인이 일치하지 않습니다."),
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "USER4003", "현재 비밀번호가 올바르지 않습니다."),

    // 사용자
    NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "사용자를 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
