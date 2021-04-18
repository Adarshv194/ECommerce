package com.ShopOnline.Buy.online.entities.order;

import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import com.fasterxml.jackson.annotation.JsonFilter;

import javax.persistence.*;

@Entity
@JsonFilter("orderProduct")
public class OrderProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;
    private Double price;
    private Double quantity;

    @OneToOne
    @JoinColumn(name = "productVariationId")
    private ProductVariation productVariation;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private ConsolidatedOrder consolidatedOrder;

    public Long getOrderProductId() {
        return orderProductId;
    }

    public void setOrderProductId(Long orderProductId) {
        this.orderProductId = orderProductId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public ProductVariation getProductVariation() {
        return productVariation;
    }

    public void setProductVariation(ProductVariation productVariation) {
        this.productVariation = productVariation;
    }

    public ConsolidatedOrder getConsolidatedOrder() {
        return consolidatedOrder;
    }

    public void setConsolidatedOrder(ConsolidatedOrder consolidatedOrder) {
        this.consolidatedOrder = consolidatedOrder;
    }
}
