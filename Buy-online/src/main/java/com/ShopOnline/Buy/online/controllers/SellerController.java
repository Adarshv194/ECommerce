package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.models.AddressUpdateModel;
import com.ShopOnline.Buy.online.models.SellerRegisterModel;
import com.ShopOnline.Buy.online.models.SellerUpdateModel;
import com.ShopOnline.Buy.online.models.UpdatePasswordModel;
import com.ShopOnline.Buy.online.services.SellerDaoService;
import com.ShopOnline.Buy.online.services.UserDaoService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class SellerController {

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    SellerDaoService sellerDaoService;

    @PostMapping(value = "/seller-registration")
    public ResponseEntity<Object> createSeller(@Valid @RequestBody SellerRegisterModel sellerRegisterModel) {
        String message = userDaoService.saveNewSeller(sellerRegisterModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping(value = "/seller-home-profile")
    public MappingJacksonValue sellerHomeProfile() {
        Seller seller = userDaoService.getLoggedInSeller();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userId","firstName","lastName",
                "active","companyContact","companyName","gst","addressSet");

        FilterProvider filters = new SimpleFilterProvider().addFilter("userfilter",filter);

        MappingJacksonValue mapping = new MappingJacksonValue(seller);
        mapping.setFilters(filters);

        return mapping;
    }

    @PatchMapping(value = "/seller-profile-update")
    public ResponseEntity<Object> updateSellerProfile(@RequestBody SellerUpdateModel sellerUpdateModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = sellerDaoService.updateSellerProfile(sellerUpdateModel, seller.getUserId());

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PostMapping(value = "/seller-update-password")
    public ResponseEntity<Object> updateSellerPassword(@Valid @RequestBody UpdatePasswordModel updatePasswordModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = sellerDaoService.updateSellerPassword(seller.getUserId(), updatePasswordModel);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @PatchMapping(value = "/seller-update-address/{addressId}")
    public ResponseEntity<Object> updateSellerAddress(@PathVariable Long addressId, @RequestBody AddressUpdateModel addressUpdateModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = sellerDaoService.updateSellerAddress(addressId, seller.getUserId(), addressUpdateModel);

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

}
