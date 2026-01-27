package com.umc.barkit.domain.favorite.exception.code;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FavoriteErrorCode implements BaseErrorCode {

    // ===== Favorite =====
    FAVORITE_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "FAVORITE4001",
            "이미 즐겨찾기한 매장입니다."
    ),

    FAVORITE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "FAVORITE4002",
            "즐겨찾기 정보를 찾을 수 없습니다."
    ),

    FAVORITE_LIMIT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "FAVORITE4003",
            "즐겨찾기는 최대 5개까지 가능합니다."
    ),

    FAVORITE_FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "FAVORITE4004",
            "해당 즐겨찾기에 대한 권한이 없습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
