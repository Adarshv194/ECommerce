package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.entities.order.OrderProduct;
import com.ShopOnline.Buy.online.entities.order.OrderStatus;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;

public class SellerViewOrderModel {
    private OrderProduct orderProduct;
    private OrderStatus orderStatus;

    public OrderProduct getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
