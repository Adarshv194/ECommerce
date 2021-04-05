package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.models.CustomerReigisterModel;
import com.ShopOnline.Buy.online.models.Email;
import com.ShopOnline.Buy.online.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    UserDaoService userDaoService;

    //////////////////////////////////////////////// ADMIN - API'S ////////////////////////////////////////////

    @PostMapping(path = "/admin/enable-seller/{sellerId}")
    public String enableSellerAccount(@PathVariable Long sellerId) {
        return userDaoService.enableSellerAccount(sellerId);
    }

    //////////////////////////////////////////////// CUSTOMER - API'S //////////////////////////////////////////

    @PostMapping("/customer-registration")
    public ResponseEntity<Object> registerCustomer(@Valid @RequestBody CustomerReigisterModel customerReigisterModel) {
        String message = userDaoService.saveNewCustomer(customerReigisterModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/confirm-account",method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
    public String confirmCustomerAccount(@RequestParam("token") String confirmationToken) {
        return userDaoService.confirmCustomerAccount(confirmationToken);
    }

    @RequestMapping(value = "/request-actToken",method = {RequestMethod.POST})
    public String resendActivationToken(@RequestBody Email email) {
        return userDaoService.resendActivationToken(email.getEmail());
    }
}
