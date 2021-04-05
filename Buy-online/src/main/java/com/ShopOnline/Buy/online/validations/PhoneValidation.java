package com.ShopOnline.Buy.online.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidation implements ConstraintValidator<Phone,String> {
    @Override
    public void initialize(Phone phone) {

    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if(phone == null) {
            return false;
        }
        if(phone.matches("^[0-9]*$"))
            return true;
        return false;
    }
}
