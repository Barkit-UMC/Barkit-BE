package com.umc.barkit.global.apiPayload.exception;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 전역 Exception
 * 모든 도메인 Exception의 최상위 부모
 */
@Getter
@RequiredArgsConstructor
public class GeneralException extends RuntimeException {

    private final BaseErrorCode code;
}
