package com.umc.barkit.domain.user.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {

    EMAIL_CHECK_OK(HttpStatus.OK, "AUTH2000", "아이디 중복 확인에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "AUTH2001", "회원가입이 성공적으로 완료되었습니다."),
    LOGIN_OK(HttpStatus.OK, "AUTH2002", "로그인에 성공했습니다."),
    TOKEN_REFRESH_OK(HttpStatus.OK, "AUTH2003", "액세스 토큰 재발급에 성공했습니다."),
    LOGOUT_OK(HttpStatus.OK, "AUTH2004", "로그아웃에 성공했습니다."),
    OAUTH_AUTHORIZE_URL_OK(HttpStatus.OK, "AUTH2005", "네이버 OAuth 인가 URL 생성에 성공했습니다."),

    PERSONAL_INFO_OK(HttpStatus.OK, "USER2000", "개인정보 조회에 성공했습니다."),
    BIRTH_DATE_UPDATED(HttpStatus.OK, "USER2001", "생년월일 변경에 성공했습니다."),
    PASSWORD_VALIDATED(HttpStatus.OK, "USER2002","현재 비밀번호가 성공적으로 검증되었습니다."),
    PASSWORD_UPDATED(HttpStatus.OK, "USER2003", "비밀번호 변경에 성공했습니다."),

    NOTIFICATION_UPDATED(HttpStatus.OK, "USER2005", "알림 설정이 변경되었습니다."),
    LOCATION_CONSENT_UPDATED(HttpStatus.OK, "USER2006", "위치 권한 동의 상태가 변경되었습니다."),
    WITHDRAW_OK(HttpStatus.OK, "USER2007", "회원탈퇴가 완료되었습니다."),

    OAUTH_STATUS_OK(HttpStatus.OK, "OAUTH2001", "소셜 연동 상태 조회에 성공했습니다."),
    OAUTH_CONNECTED_OK(HttpStatus.OK, "OAUTH2002", "소셜 연동에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
