package com.ShopOnline.Buy.online.validations;

import com.ShopOnline.Buy.online.models.CustomerReigisterModel;
import com.ShopOnline.Buy.online.models.ForgotPasswordModel;
import com.ShopOnline.Buy.online.models.SellerRegisterModel;
import com.ShopOnline.Buy.online.models.UpdatePasswordModel;

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
            ForgotPasswordModel model = (ForgotPasswordModel) obj;
            return model.getPassword().equals(model.getConfirmPassword());
        }

        else if(obj instanceof UpdatePasswordModel) {
            UpdatePasswordModel model = (UpdatePasswordModel) obj;
            return model.getNewPassword().equals(model.getConfirmPassword());
        }

        else {
            CustomerReigisterModel model = (CustomerReigisterModel) obj;
            return model.getPassword().equals(model.getConfirmPassword());
        }
    }
}
