package com.ShopOnline.Buy.online.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ZipcodeValidation implements ConstraintValidator<Zipcode,String> {
    @Override
    public void initialize(Zipcode constraintAnnotation) {

    }

    @Override
    public boolean isValid(String zipcode, ConstraintValidatorContext context) {
        if(zipcode == null) {
            return false;
        }

        if(zipcode.matches("^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$")) {
            return true;
        }
        else {
            return false;
        }
    }
}
