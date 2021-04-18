package com.ShopOnline.Buy.online.entities.order;

import com.fasterxml.jackson.annotation.JsonFilter;

import javax.persistence.*;

@Entity
@JsonFilter("orderStatus")
public class OrderStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderStatusId;
    private FromStatus fromStatus;
    private ToStatus toStatus;
    private String transitionNotesComments;

    @OneToOne
    @JoinColumn(name = "orderProductId")
    private OrderProduct orderProduct;

    public Long getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Long orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public FromStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(FromStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public ToStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(ToStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getTransitionNotesComments() {
        return transitionNotesComments;
    }

    public void setTransitionNotesComments(String transitionNotesComments) {
        this.transitionNotesComments = transitionNotesComments;
    }

    public OrderProduct getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
    }
}
