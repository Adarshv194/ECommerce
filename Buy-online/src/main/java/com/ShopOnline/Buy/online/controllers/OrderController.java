package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.entities.order.FromStatus;
import com.ShopOnline.Buy.online.models.OrderViewModel;
import com.ShopOnline.Buy.online.services.OrderDaoService;
import com.ShopOnline.Buy.online.services.UserDaoService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
public class OrderController {

    @Autowired
    OrderDaoService orderDaoService;
    @Autowired
    UserDaoService userDaoService;

    @PostMapping("/customer/order-all-cart-products/{addressId}")
    public ResponseEntity<Object> orderAllProductsFromCart(@PathVariable Long addressId) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = orderDaoService.orderAllProductsFromCart(customer, addressId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/customer/order-partial-products/{addressId}")
    public ResponseEntity<Object> orderPartialProducts(@PathVariable Long addressId, @RequestBody Set<Long> productVariationIdSet) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = orderDaoService.orderPartialProducts(productVariationIdSet, customer, addressId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/customer/order-product-directly/{productVariationId}/{quantity}/{addressId}")
    public ResponseEntity<Object> orderProductDirectly(@PathVariable Long productVariationId, @PathVariable Integer quantity, @PathVariable Long addressId) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = orderDaoService.orderProductDirectly(customer, productVariationId, quantity, addressId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PatchMapping("/customer/order-cancel/{orderProductId}")
    public ResponseEntity<Object> orderCancel(@PathVariable Long orderProductId) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = orderDaoService.orderCancel(customer,orderProductId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PatchMapping("/customer/order-return/{orderProductId}")
    public ResponseEntity<Object> orderReturn(@PathVariable Long orderProductId) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = orderDaoService.orderReturn(customer,orderProductId);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/customer/view-order/{orderId}")
    public MappingJacksonValue viewOrder(@PathVariable Long orderId) {
        Customer customer = userDaoService.getLoggedInCustomer();
        OrderViewModel orderViewModel = orderDaoService.viewOrder(customer, orderId);

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("totalProductsOrdered","amountPaid","paymentMethod"
        ,"customerAddressLine","customerAddressState","customerAddressCity","customerAddressZipCode","customerAddressCountry","customerAddressLabel"
                ,"specialInformation","dateCreated");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("productVariation","price","quantity");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price",
                "active","productAttributes");

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("consolidatedOrder",filter1)
                .addFilter("orderProduct",filter2)
                .addFilter("variantFilter",filter3);


        MappingJacksonValue mapping = new MappingJacksonValue(orderViewModel);
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping("/customer/view-all-orders")
    public MappingJacksonValue viewAllOrders(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {
        Customer customer = userDaoService.getLoggedInCustomer();

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("totalProductsOrdered","amountPaid","paymentMethod"
                ,"customerAddressLine","customerAddressState","customerAddressCity","customerAddressZipCode","customerAddressCountry","customerAddressLabel"
                ,"specialInformation","dateCreated");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("productVariation","price","quantity");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price",
                "active","productAttributes");

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("consolidatedOrder",filter1)
                .addFilter("orderProduct",filter2)
                .addFilter("variantFilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(orderDaoService.viewAllOrders(customer,page,size));
        mapping.setFilters(filters);

        return mapping;
    }

    @DeleteMapping("/customer/delete-all-orders")
    public ResponseEntity<Object> deleteAllOrders() {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = orderDaoService.deleteAllOrders(customer);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @GetMapping("/seller/view-all-orders")
    public MappingJacksonValue sellerViewAllOrders(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {
        Seller seller = userDaoService.getLoggedInSeller();

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("fromStatus","toStatus","transitionNotesComments");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("orderProductId","productVariation","price","quantity");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("productVariationId","variantName","price",
                "active","productAttributes");

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("orderStatus",filter1)
                .addFilter("orderProduct",filter2)
                .addFilter("variantFilter",filter3);


        MappingJacksonValue mapping = new MappingJacksonValue(orderDaoService.sellerViewAllOrders(seller, page, size));
        mapping.setFilters(filters);

        return mapping;
    }

    @PatchMapping("/seller/update-order-status/{orderProductId}/{fromStatus}/{toStatus}")
    public ResponseEntity<Object> sellerUpdateOrderStatus(@PathVariable Long orderProductId, @PathVariable String fromStatus, @PathVariable String toStatus) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = orderDaoService.sellerUpdateOrderStatus(seller, orderProductId, fromStatus, toStatus);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @GetMapping("/admin/view-all-orders")
    public MappingJacksonValue adminViewAllOrders(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("fromStatus","toStatus","transitionNotesComments");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("orderProductId","productVariation","price","quantity");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("productVariationId","variantName","price",
                "active","productAttributes");

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("orderStatus",filter1)
                .addFilter("orderProduct",filter2)
                .addFilter("variantFilter",filter3);


        MappingJacksonValue mapping = new MappingJacksonValue(orderDaoService.adminViewAllOrders(page, size));
        mapping.setFilters(filters);

        return mapping;
    }

    @PatchMapping("/admin/update-order-status/{orderProductId}/{fromStatus}/{toStatus}")
    public ResponseEntity<Object> adminUpdateOrderStatus(@PathVariable Long orderProductId, @PathVariable String fromStatus, @PathVariable String toStatus) {

        String message = orderDaoService.adminUpdateOrderStatus(orderProductId, fromStatus, toStatus);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

}
