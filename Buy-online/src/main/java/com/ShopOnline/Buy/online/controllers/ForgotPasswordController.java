package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.models.EmailModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ForgotPasswordController {

    @PostMapping(value = "/forgot-password")
    public String forgotPassword(@Valid @RequestBody EmailModel emailModel) {

    }
}
