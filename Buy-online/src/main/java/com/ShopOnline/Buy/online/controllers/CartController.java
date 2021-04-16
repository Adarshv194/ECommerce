package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.order.Cart;
import com.ShopOnline.Buy.online.models.CartViewModel;
import com.ShopOnline.Buy.online.services.CartDaoService;
import com.ShopOnline.Buy.online.services.UserDaoService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {

    @Autowired
    CartDaoService cartDaoService;
    @Autowired
    UserDaoService userDaoService;

    @PostMapping("/customer/cart-add-product/{productVariationId}/{quantity}")
    public ResponseEntity<Object> addProductToCart(@PathVariable Long productVariationId, @PathVariable Integer quantity) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = cartDaoService.addProductToCart(productVariationId,quantity,customer);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/customer/cart-all-products")
    public MappingJacksonValue getAllProductsVariationForCustomer() {
        Customer customer = userDaoService.getLoggedInCustomer();

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","productAttributes","price");

        FilterProvider filters = new SimpleFilterProvider().addFilter("variantFilter",filter1);

        MappingJacksonValue mapping = new MappingJacksonValue(cartDaoService.getAllProductsVariationForCustomer(customer));
        mapping.setFilters(filters);

        return mapping;
    }

    @DeleteMapping("/customer/cart-delete-product/{productVariationId}")
    public ResponseEntity<Object> deleteProductFromCart(@PathVariable Long productVariationId) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = cartDaoService.deleteProductFromCart(productVariationId,customer);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping("/customer/cart-update-product/{productVariationId}/{quantity}")
    public MappingJacksonValue updateCart(@PathVariable Long productVariationId, @PathVariable Integer quantity) {
        Customer customer = userDaoService.getLoggedInCustomer();

        CartViewModel cartViewModel = cartDaoService.updateCart(productVariationId,quantity,customer);

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","productAttributes","price");

        FilterProvider filters = new SimpleFilterProvider().addFilter("variantFilter",filter1);

        MappingJacksonValue mapping = new MappingJacksonValue(cartViewModel);
        mapping.setFilters(filters);

        return mapping;


    }

    @DeleteMapping("/customer/cart-empty")
    public ResponseEntity<Object> emptyCart() {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = cartDaoService.emptyCart(customer);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
}
