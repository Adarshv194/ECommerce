package com.ShopOnline.Buy.online.validations;

import com.ShopOnline.Buy.online.models.CustomerReigisterModel;
import com.ShopOnline.Buy.online.models.ForgotPasswordModel;
import com.ShopOnline.Buy.online.models.SellerRegisterModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidation implements ConstraintValidator<PasswordMatches,Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if(obj instanceof SellerRegisterModel) {
            SellerRegisterModel model = (SellerRegisterModel) obj;
            return model.getPassword().equals(model.getConfirmPassword());
        }

        else if(obj instanceof ForgotPasswordModel) {
            return true;
        }

        else {
            CustomerReigisterModel model = (CustomerReigisterModel) obj;
            return model.getPassword().equals(model.getConfirmPassword());
        }
    }
}
