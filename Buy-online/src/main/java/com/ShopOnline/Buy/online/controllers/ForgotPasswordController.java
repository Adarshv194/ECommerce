package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.models.EmailModel;
import com.ShopOnline.Buy.online.models.ForgotPasswordModel;
import com.ShopOnline.Buy.online.services.ForgotPasswordDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ForgotPasswordController {

    @Autowired
    ForgotPasswordDaoService forgotPasswordDaoService;

    @PostMapping(value = "/forgot-password")
    public String forgotPassword(@Valid @RequestBody EmailModel emailModel) {
        return forgotPasswordDaoService.forgotPassword(emailModel.getEmail());
    }

    @PutMapping(value = "/reset-password")
    public String resetPassword(@RequestParam("token") String token, @Valid @RequestBody ForgotPasswordModel forgotPasswordModel) {
        return forgotPasswordDaoService.resetPassword(token, forgotPasswordModel);
    }
}
