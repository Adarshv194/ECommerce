package com.ShopOnline.Buy.online.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductUpdateVariationModel {
    private String variantName;
    private Integer quantityAvailable;
    private Integer price;
    private Map<String,Object> productAttributes = new LinkedHashMap<>();

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

    public Map<String, Object> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Map<String, Object> productAttributes) {
        this.productAttributes = productAttributes;
    }
}
