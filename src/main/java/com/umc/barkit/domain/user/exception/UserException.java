package com.umc.barkit.domain.user.exception;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
