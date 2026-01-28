package com.umc.barkit.global.validator;

import com.umc.barkit.global.annotation.ValidCursor;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CursorValidator implements ConstraintValidator<ValidCursor, Integer> {

    private long min;

    @Override
    public void initialize(ValidCursor constraintAnnotation) {
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value >= min;
    }
}
