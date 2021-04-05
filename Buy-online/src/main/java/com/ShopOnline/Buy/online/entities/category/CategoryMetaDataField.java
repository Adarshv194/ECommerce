package com.ShopOnline.Buy.online.entities.category;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@JsonFilter("categoryfilter")
public class CategoryMetaDataField {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long categoryMetaDataFieldId;

    private String name;

    public Long getCategoryMetaDataFieldId() {
        return categoryMetaDataFieldId;
    }

    public void setCategoryMetaDataFieldId(Long categoryMetaDataFieldId) {
        this.categoryMetaDataFieldId = categoryMetaDataFieldId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
