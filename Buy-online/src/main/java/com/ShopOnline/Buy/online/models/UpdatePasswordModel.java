package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.validations.Password;
import com.ShopOnline.Buy.online.validations.PasswordMatches;

import javax.validation.constraints.NotNull;

@PasswordMatches
public class UpdatePasswordModel {
    @NotNull
    private String oldPassword;

    @Password
    @NotNull
    private String newPassword;

    @NotNull
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
