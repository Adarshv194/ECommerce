package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.services.AdminDaoService;
import com.ShopOnline.Buy.online.services.ProductDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @Autowired
    ProductDaoService productDaoService;
    @Autowired
    AdminDaoService adminDaoService;

    @PatchMapping(value = "/admin/activate-customer/{customerId}")
    public ResponseEntity<Object> activateCustomer(@PathVariable Long customerId) {
        String message = adminDaoService.activateCustomer(customerId);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/admin/deactivate-customer/{customerId}")
    public ResponseEntity<Object> deActivateCustomer(@PathVariable Long customerId) {
        String message = adminDaoService.deActivateCustomer(customerId);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/admin/activate-seller/{sellerId}")
    public ResponseEntity<Object> activateSeller(@PathVariable Long sellerId) {
        String message = adminDaoService.activateSeller(sellerId);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/admin/deactivate-seller/{sellerId}")
    public ResponseEntity<Object> deActivateSeller(@PathVariable Long sellerId) {
        String message = adminDaoService.deActivateSeller(sellerId);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/admin/activate-product/{productId}")
    public ResponseEntity<Object> activateProduct(@PathVariable Long productId) {
        String message = productDaoService.activateProduct(productId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/admin/deactivate-product/{productId}")
    public ResponseEntity<Object> deActivateProduct(@PathVariable Long productId) {
        String message = productDaoService.deActivatedProduct(productId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
}
