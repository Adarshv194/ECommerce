package com.ShopOnline.Buy.online.utils;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CategoryMetaDataFieldValuesID implements Serializable {
    private Long categoryMetaDataFieldId;
    private Long categoryId;

    public Long getCategoryMetaDataFieldId() {
        return categoryMetaDataFieldId;
    }

    public void setCategoryMetaDataFieldId(Long categoryMetaDataFieldId) {
        this.categoryMetaDataFieldId = categoryMetaDataFieldId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
