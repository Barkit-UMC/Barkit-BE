package com.umc.barkit.domain.store.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {

    CENTER_LOCATION_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "MAP4001",
            "distanceType이 CENTER인 경우 centerLat와 centerLng는 필수입니다."
    ),

    CENTER_LOCATION_PAIR_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "MAP4002",
            "centerLat와 centerLng는 함께 전달되어야 합니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
