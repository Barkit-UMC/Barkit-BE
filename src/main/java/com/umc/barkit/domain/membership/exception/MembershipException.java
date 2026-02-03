package com.umc.barkit.domain.membership.exception;

import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;

public class MembershipException extends GeneralException {
    public MembershipException(BaseErrorCode code) {
        super(code);
    }
}
