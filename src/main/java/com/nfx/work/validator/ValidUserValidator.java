package com.nfx.work.validator;

import com.nfx.work.frontdto.RegisterUser;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class ValidUserValidator implements ConstraintValidator<ValidUser, RegisterUser> {

    private static final String USER_NOT_ADULT = "User is not adult";
    private static final String EMPTY_FIELDS = "Fields cannot be empty";
    private static final String PESEL_TOO_SHORT = "This is not a pesel";

    @Override
    public boolean isValid(RegisterUser value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if(StringUtils.isBlank(value.getName()) ||
                StringUtils.isBlank(value.getSurname()) ||
                StringUtils.isBlank(value.getPesel()) ||
                Objects.isNull(value.getMoney())) {
            context.buildConstraintViolationWithTemplate(EMPTY_FIELDS).addConstraintViolation();
            return false;
        }

        if(value.getPesel().length() != 11 ) {
            context.buildConstraintViolationWithTemplate(PESEL_TOO_SHORT).addConstraintViolation();
            return false;
        }

        int yearOfBirthday = Integer.parseInt("20"+value.getPesel().substring(0,2));
        int yearType = Integer.parseInt(value.getPesel().substring(2,3));
        int year = LocalDate.now().getYear();
        if(yearType > 1 && (year - yearOfBirthday < 18)) {
            context.buildConstraintViolationWithTemplate(USER_NOT_ADULT).addConstraintViolation();
            return false;
        }

        return true;
    }
}
