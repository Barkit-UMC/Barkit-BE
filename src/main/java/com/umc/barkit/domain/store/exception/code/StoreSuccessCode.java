package com.umc.barkit.domain.store.exception.code;


import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StoreSuccessCode implements BaseSuccessCode {
    FOUND(HttpStatus.OK,"STOREDETAIL200_1","성공적으로 매장 상세정보를 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
