package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.entities.order.ConsolidatedOrder;
import com.ShopOnline.Buy.online.entities.order.OrderProduct;

import java.util.List;

public class OrderViewModel {
    private ConsolidatedOrder consolidatedOrder;
    private List<OrderProduct> orderProductList;

    public ConsolidatedOrder getConsolidatedOrder() {
        return consolidatedOrder;
    }

    public void setConsolidatedOrder(ConsolidatedOrder consolidatedOrder) {
        this.consolidatedOrder = consolidatedOrder;
    }

    public List<OrderProduct> getOrderProductList() {
        return orderProductList;
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }
}
