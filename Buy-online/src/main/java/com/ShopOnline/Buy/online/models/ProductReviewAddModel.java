package com.ShopOnline.Buy.online.models;

import javax.validation.constraints.NotNull;

public class ProductReviewAddModel {
    @NotNull
    private String review;

    @NotNull
    private Integer rating;

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
