package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataFieldValues;

import java.util.List;

public class CategoryViewModel {
    Category category;
    List<String> brand;
    List<CategoryMetaDataFieldValues> categoryMetaDataFieldValuesList;

    public List<String> getBrand() {
        return brand;
    }

    public void setBrand(List<String> brand) {
        this.brand = brand;
    }

    public List<CategoryMetaDataFieldValues> getCategoryMetaDataFieldValuesList() {
        return categoryMetaDataFieldValuesList;
    }

    public void setCategoryMetaDataFieldValuesList(List<CategoryMetaDataFieldValues> categoryMetaDataFieldValuesList) {
        this.categoryMetaDataFieldValuesList = categoryMetaDataFieldValuesList;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
