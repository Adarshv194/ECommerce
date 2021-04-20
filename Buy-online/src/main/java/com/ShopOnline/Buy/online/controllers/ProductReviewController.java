package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.models.ProductReviewAddModel;
import com.ShopOnline.Buy.online.services.ProductReviewDaoService;
import com.ShopOnline.Buy.online.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class ProductReviewController {

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    ProductReviewDaoService productReviewDaoService;

    @PostMapping("/customer/add-product-review/{orderProductId}")
    public ResponseEntity<Object> addProductReview(@PathVariable Long orderProductId,@Valid @RequestBody ProductReviewAddModel productReviewAddModel) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = productReviewDaoService.addProductReview(customer, productReviewAddModel, orderProductId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
}
