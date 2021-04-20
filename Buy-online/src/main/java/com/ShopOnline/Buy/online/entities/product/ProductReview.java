package com.ShopOnline.Buy.online.entities.product;

import com.ShopOnline.Buy.online.entities.Customer;

import javax.persistence.*;

@Entity
public class ProductReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productReviewId;
    private String review;
    private Integer rating;

    @OneToOne
    @JoinColumn(name = "customerUserId")
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "productVariationId")
    private ProductVariation productVariation;

    public Long getProductReviewId() {
        return productReviewId;
    }

    public void setProductReviewId(Long productReviewId) {
        this.productReviewId = productReviewId;
    }

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ProductVariation getProductVariation() {
        return productVariation;
    }

    public void setProductVariation(ProductVariation productVariation) {
        this.productVariation = productVariation;
    }
}
