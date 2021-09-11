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
    private static final String NOT_VALUE_DATE = "This date is not valid";

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

        int mouth = Integer.parseInt(value.getPesel().substring(2,4));
        int day = Integer.parseInt(value.getPesel().substring(4,6));
        int yearType = Integer.parseInt(value.getPesel().substring(2,3));
        int yearOfBirthday = Integer.parseInt((yearType> 1 ? "20" : "19") + value.getPesel().substring(0,2));
        int currentYear = LocalDate.now().getYear();
        try {
            if(yearType > 1) {
                LocalDate.of(yearOfBirthday,mouth-20,day);
            } else {
                LocalDate.of(yearOfBirthday,mouth,day);
            }
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate(NOT_VALUE_DATE).addConstraintViolation();
            return false;
        }
        if(yearType > 1 && (currentYear - yearOfBirthday < 18)) {
            context.buildConstraintViolationWithTemplate(USER_NOT_ADULT).addConstraintViolation();
            return false;
        }

        return true;
    }
}
