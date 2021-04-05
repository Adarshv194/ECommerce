package com.ShopOnline.Buy.online.models;

import javax.validation.constraints.NotNull;

public class ProductModel {
    @NotNull
    private String productName;

    @NotNull
    private String brand;

    @NotNull
    private String productDescription;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
