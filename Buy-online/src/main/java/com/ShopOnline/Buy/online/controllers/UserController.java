package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.models.*;
import com.ShopOnline.Buy.online.services.UserDaoService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    TokenStore tokenStore;

    @PostMapping(value = "/doLogout")
    public String doLogout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);

            return "User gets logout successfully";
        }
        else {
            throw new BadRequestException("Authorization token not found, Please provide an valid token");
        }
    }

    //////////////////////////////////////////////// ADMIN - API'S ////////////////////////////////////////////

    @GetMapping(value = "/admin/get-all-customers")
    public MappingJacksonValue getAllCustomers(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size ) {
        return userDaoService.getAllCustomers(page, size);
    }

    @GetMapping(value = "/admin/get-all-sellers")
    public MappingJacksonValue getAllSellers(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {
        return userDaoService.getAllSellers(page, size);
    }




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

    @GetMapping(value = "/customer-home-profile")
    public MappingJacksonValue customerHomeProfile() {
        Customer customer = userDaoService.getLoggedInCustomer();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userId","firstName","lastName",
                "active","contact");

        FilterProvider filters = new SimpleFilterProvider().addFilter("userfilter",filter);

        MappingJacksonValue mapping = new MappingJacksonValue(customer);
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping(value = "/customer-home-profile-address")
    public MappingJacksonValue customerAddress() {
        Customer customer = userDaoService.getLoggedInCustomer();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("addressSet");

        FilterProvider filters = new SimpleFilterProvider().addFilter("userfilter",filter);

        MappingJacksonValue mapping = new MappingJacksonValue(customer);
        mapping.setFilters(filters);

        return mapping;
    }

    @PatchMapping(value = "/customer-profile-update")
    public ResponseEntity<Object> updateCustomerProfile(@RequestBody CustomerUpdateModel customerUpdateModel) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = userDaoService.updateCustomerProfile(customerUpdateModel, customer.getUserId());

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/customer-update-password")
    public ResponseEntity<Object> updateCustomerPassword(@Valid @RequestBody UpdatePasswordModel updatePasswordModel) {
        Customer customer = userDaoService.getLoggedInCustomer();
        System.out.println("called");

        String message = userDaoService.updateCustomerPassword(updatePasswordModel, customer.getUsername());

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PostMapping(value = "/customer/add-address")
    public ResponseEntity<Object> customerAddAddress(@Valid @RequestBody AddressModel addressModel) {
        Customer customer = userDaoService.getLoggedInCustomer();
        String message = userDaoService.customerAddAddress(addressModel, customer.getUserId());

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/customer/delete-address/{addressId}")
    public ResponseEntity<Object> customerDeleteAddress(@PathVariable Long addressId) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = userDaoService.customerDeleteAddress(addressId, customer.getUserId());

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/customer/update-address/{addressId}")
    public ResponseEntity<Object> customerUpdateAddress(@PathVariable Long addressId, @RequestBody AddressUpdateModel addressUpdateModel) {
        Customer customer = userDaoService.getLoggedInCustomer();

        String message = userDaoService.customerUpdateAddress(customer.getUserId(), addressId, addressUpdateModel);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }
}
