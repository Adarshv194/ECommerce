package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.services.AccountUnlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountUnlockController {

    @Autowired
    AccountUnlockService accountUnlockService;

    @GetMapping(value = "/user-account-unlock/{username}")
    public ResponseEntity<Object> unlockAccount(@PathVariable String username){
        String message = accountUnlockService.unlockAccount(username);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping(value = "/do-unlock")
    public ResponseEntity<Object> unlockAccountSuccess(@RequestParam String username) {
        String message = accountUnlockService.unlockAccountSuccess(username);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
}
