package com.umc.barkit.domain.user.exception;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;

public class TermException extends GeneralException {
    public TermException(BaseErrorCode code){
        super(code);
    }
}
