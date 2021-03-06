package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.validations.Password;
import com.ShopOnline.Buy.online.validations.PasswordMatches;

import javax.validation.constraints.NotNull;

@PasswordMatches
public class ForgotPasswordModel {
    @Password
    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
