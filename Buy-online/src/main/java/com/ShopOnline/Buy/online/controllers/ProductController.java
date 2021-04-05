package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.models.ProductModel;
import com.ShopOnline.Buy.online.models.ProductUpdateModel;
import com.ShopOnline.Buy.online.models.ProductUpdateVariationModel;
import com.ShopOnline.Buy.online.models.ProductVariationModel;
import com.ShopOnline.Buy.online.repos.ProductVariationRepository;
import com.ShopOnline.Buy.online.services.ProductDaoService;
import com.ShopOnline.Buy.online.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ProductController {

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    ProductDaoService productDaoService;
    @Autowired
    ProductVariationRepository productVariationRepository;

    @PostMapping(value = "/add-product/{categoryName}")
    public ResponseEntity<Object> saveProduct(@PathVariable String categoryName, @Valid @RequestBody ProductModel productModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = productDaoService.addProduct(categoryName,seller,productModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping(value = "/add-product-variation/{productId}")
    public ResponseEntity<Object> saveProductVariation(@PathVariable Long productId,@Valid @RequestBody ProductVariationModel productVariationModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = productDaoService.addProductVariation(productId, seller, productVariationModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/update-product/{productId}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateModel productModel) {
        Seller seller = userDaoService.getLoggedInSeller();
        String message = productDaoService.updateProduct(productId, productModel, seller);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/update-product-variation/{productVariationId}")
    public ResponseEntity<Object> updateProductvariation(@PathVariable Long productVariationId, @RequestBody ProductUpdateVariationModel productUpdateVariationModel) {
        Seller seller = userDaoService.getLoggedInSeller();
        String message = productDaoService.updateProductVariaiton(productVariationId, productUpdateVariationModel, seller);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    }
