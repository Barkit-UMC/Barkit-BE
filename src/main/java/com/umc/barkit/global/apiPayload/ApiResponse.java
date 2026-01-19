package com.umc.barkit.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import com.umc.barkit.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;

    private final String code;
    private final String message;
    private final T result;

    //  성공 (result 포함)
    public static <T> ApiResponse<T> onSuccess(
            BaseSuccessCode successCode,
            T result
    ) {
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                result
        );
    }

    //  실패
    public static <T> ApiResponse<T> onFailure(
            BaseErrorCode errorCode,
            T result
    ) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                result
        );
    }
}
