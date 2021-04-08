package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.entities.product.Product;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import com.ShopOnline.Buy.online.models.ProductModel;
import com.ShopOnline.Buy.online.models.ProductUpdateModel;
import com.ShopOnline.Buy.online.models.ProductUpdateVariationModel;
import com.ShopOnline.Buy.online.models.ProductVariationModel;
import com.ShopOnline.Buy.online.repos.ProductVariationRepository;
import com.ShopOnline.Buy.online.services.ProductDaoService;
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
import java.util.List;

@RestController
public class ProductController {

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    ProductDaoService productDaoService;
    @Autowired
    ProductVariationRepository productVariationRepository;

    @PostMapping(value = "/add-product/{categoryId}")
    public ResponseEntity<Object> saveProduct(@PathVariable Long categoryId, @Valid @RequestBody ProductModel productModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = productDaoService.addProduct(categoryId,seller,productModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping(value = "/add-product-variation/{productId}")
    public ResponseEntity<Object> saveProductVariation(@PathVariable Long productId,@Valid @RequestBody ProductVariationModel productVariationModel) {
        Seller seller = userDaoService.getLoggedInSeller();

        String message = productDaoService.addProductVariation(productId, seller, productVariationModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    public Product sellerViewProduct(Long productId) {

        return productDaoService.findProductForSeller(productId);
    }

    @GetMapping(value = "/seller/view-product/{productId}")
    public MappingJacksonValue sellerGetProduct(@PathVariable Long productId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(sellerViewProduct(productId));
        mapping.setFilters(filters);

        return mapping;
    }

    public ProductVariation sellerViewProductVaraition(Long productVariaitonId) {
        return productDaoService.findProductVariationForSeller(productVariaitonId);
    }

    @GetMapping(value = "/seller/view-product-variation/{productVariaitonId}")
     public MappingJacksonValue sellerGetProductVariation(@PathVariable Long productVariaitonId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes","product");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","category");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("variantFilter",filter1)
                .addFilter("productfilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(sellerViewProductVaraition(productVariaitonId));
        mapping.setFilters(filters);

        return mapping;
    }

    public List<Product> findSellerWiseProducts(Long sellerId) {
        return productDaoService.findSellerWiseAllProducts(sellerId);
    }

    @GetMapping(value = "/seller/view-allproducts")
    public MappingJacksonValue sellerGetAllProducts() {
        Seller seller = userDaoService.getLoggedInSeller();

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(findSellerWiseProducts(seller.getUserId()));
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping(value = "/seller/view-all-product-variation/{productId}")
    public MappingJacksonValue sellerGetAllProductVariationsByProduct(@PathVariable Long productId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "isCancellable","isReturnable","productVariationSet");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "isActive","productAttributes");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2);

        MappingJacksonValue mapping = new MappingJacksonValue(sellerViewProduct(productId));
        mapping.setFilters(filters);

        return mapping;
    }

    @DeleteMapping(value = "/seller/delete-product/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long productId) {
        Seller seller = userDaoService.getLoggedInSeller();
        String message = productDaoService.deleteProduct(productId, seller.getUserId());

        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    public Product customerViewProduct(Long productId) {
        return productDaoService.customerViewProduct(productId);
    }

    @GetMapping(value = "/customer/view-product/{productId}")
    public MappingJacksonValue customerGetProduct(@PathVariable Long productId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(customerViewProduct(productId));
        mapping.setFilters(filters);

        return mapping;
    }

    public List<Product> customerFindAllProductsCategoryWise(Long categoryId) {
        return productDaoService.customerFindAllProductsCategoryWise(categoryId);
    }

    @GetMapping(value = "/customer/view-allproducts/{categoryId}")
    public MappingJacksonValue customerGetAllProductsForACathegory(@PathVariable Long categoryId) {
        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(customerFindAllProductsCategoryWise(categoryId));
        mapping.setFilters(filters);

        return mapping;
    }

    public List<Product> customerFindAllSimilarProducts(Long productId) {
        return productDaoService.customerGetAllSimilarProduct(productId);
    }

    @GetMapping(value = "/customer/view-similar-products/{productId}")
    public MappingJacksonValue customerGetAllSimilarProduct(@PathVariable Long productId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(customerFindAllSimilarProducts(productId));
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping(value = "/admin/view-product/{productId}")
    public MappingJacksonValue adminGetProduct(@PathVariable Long productId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(customerViewProduct(productId));
        mapping.setFilters(filters);

        return mapping;
    }

    List<Product> adminFindAllProducts() {
        return productDaoService.adminFindAllProducts();
    }

    @GetMapping(value = "/admin/view-allproducts")
    public MappingJacksonValue adminGetAllProducts() {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("productName","brand","productDescription",
                "cancellable","returnable","productVariationSet","category");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("variantName","price","quantityAvailable",
                "active","productAttributes");

        SimpleBeanPropertyFilter filter3 = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("productfilter",filter1)
                .addFilter("variantFilter",filter2)
                .addFilter("categoryfilter",filter3);

        MappingJacksonValue mapping = new MappingJacksonValue(adminFindAllProducts());
        mapping.setFilters(filters);

        return mapping;
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
