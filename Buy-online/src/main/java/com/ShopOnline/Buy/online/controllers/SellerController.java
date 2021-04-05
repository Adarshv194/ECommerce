package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.models.SellerRegisterModel;
import com.ShopOnline.Buy.online.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SellerController {

    @Autowired
    UserDaoService userDaoService;

    @PostMapping(path = "/seller-registration")
    public ResponseEntity<Object> createSeller(@Valid @RequestBody SellerRegisterModel sellerRegisterModel) {
        String message = userDaoService.saveNewSeller(sellerRegisterModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }


}
