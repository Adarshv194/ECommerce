package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.validations.Email;

public class EmailModel {
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
