package com.umc.barkit.domain.home.exception;

import com.umc.barkit.global.apiPayload.exception.GeneralException;
import com.umc.barkit.domain.home.exception.code.HomeErrorCode;

public class HomeException extends GeneralException {
    public HomeException(HomeErrorCode code) {
        super(code);
    }
}