package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataFieldValues;

import java.util.ArrayList;
import java.util.List;

public class FilterCategoryModel {
    Category category;
    List<CategoryMetaDataFieldValues> categoryMetaDataFieldValuesList;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<CategoryMetaDataFieldValues> getCategoryMetaDataFieldValuesList() {
        return categoryMetaDataFieldValuesList;
    }

    public void setCategoryMetaDataFieldValuesList(List<CategoryMetaDataFieldValues> categoryMetaDataFieldValuesList) {
        this.categoryMetaDataFieldValuesList = categoryMetaDataFieldValuesList;
    }
}
