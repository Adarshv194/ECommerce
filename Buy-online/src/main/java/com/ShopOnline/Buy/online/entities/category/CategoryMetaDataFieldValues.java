package com.ShopOnline.Buy.online.entities.category;

import com.ShopOnline.Buy.online.utils.CategoryMetaDataFieldValuesID;
import com.fasterxml.jackson.annotation.JsonFilter;

import javax.persistence.*;

@Entity
@JsonFilter("categorymdfv")
public class CategoryMetaDataFieldValues {
    @EmbeddedId
    private CategoryMetaDataFieldValuesID id = new CategoryMetaDataFieldValuesID();

    @ManyToOne
    @MapsId("categoryId")
    private Category category;

    @ManyToOne
    @MapsId("categoryMetaDataFieldId")
    private CategoryMetaDataField categoryMetaDataField;

    private String fieldvalues;

    public CategoryMetaDataFieldValuesID getId() {
        return id;
    }

    public void setId(CategoryMetaDataFieldValuesID id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CategoryMetaDataField getCategoryMetaDataField() {
        return categoryMetaDataField;
    }

    public void setCategoryMetaDataField(CategoryMetaDataField categoryMetaDataField) {
        this.categoryMetaDataField = categoryMetaDataField;
    }

    public String getFieldvalues() {
        return fieldvalues;
    }

    public void setFieldvalues(String fieldvalues) {
        this.fieldvalues = fieldvalues;
    }
}
