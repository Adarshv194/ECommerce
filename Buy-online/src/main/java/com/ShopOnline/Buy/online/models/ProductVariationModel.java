package com.ShopOnline.Buy.online.models;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductVariationModel {
    @NotNull
    private String variantName;

    @NotNull
    private Integer quantityAvailable;

    @NotNull
    private Integer price;

    @NotNull
    private Map<String,String> productAttributes = new LinkedHashMap<>();

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Map<String, String> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Map<String, String> productAttributes) {
        this.productAttributes = productAttributes;
    }
}
