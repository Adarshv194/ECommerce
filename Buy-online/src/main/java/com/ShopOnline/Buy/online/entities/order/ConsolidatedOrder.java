package com.ShopOnline.Buy.online.entities.order;

import com.ShopOnline.Buy.online.entities.Customer;
import com.fasterxml.jackson.annotation.JsonFilter;

import javax.persistence.*;
import java.util.Date;

@Entity
@JsonFilter("consolidatedOrder")
public class ConsolidatedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Integer totalProductsOrdered;
    private Double amountPaid;
    private String paymentMethod;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private String customerAddressLine;
    private String customerAddressZipCode;
    private String customerAddressLabel;
    private String specialInformation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @OneToOne
    @JoinColumn(name = "customerUserId")
    private Customer customer;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getTotalProductsOrdered() {
        return totalProductsOrdered;
    }

    public void setTotalProductsOrdered(Integer totalProductsOrdered) {
        this.totalProductsOrdered = totalProductsOrdered;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerAddressCity() {
        return customerAddressCity;
    }

    public void setCustomerAddressCity(String customerAddressCity) {
        this.customerAddressCity = customerAddressCity;
    }

    public String getCustomerAddressState() {
        return customerAddressState;
    }

    public void setCustomerAddressState(String customerAddressState) {
        this.customerAddressState = customerAddressState;
    }

    public String getCustomerAddressCountry() {
        return customerAddressCountry;
    }

    public void setCustomerAddressCountry(String customerAddressCountry) {
        this.customerAddressCountry = customerAddressCountry;
    }

    public String getCustomerAddressLine() {
        return customerAddressLine;
    }

    public void setCustomerAddressLine(String customerAddressLine) {
        this.customerAddressLine = customerAddressLine;
    }

    public String getCustomerAddressZipCode() {
        return customerAddressZipCode;
    }

    public void setCustomerAddressZipCode(String customerAddressZipCode) {
        this.customerAddressZipCode = customerAddressZipCode;
    }

    public String getCustomerAddressLabel() {
        return customerAddressLabel;
    }

    public void setCustomerAddressLabel(String customerAddressLabel) {
        this.customerAddressLabel = customerAddressLabel;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getSpecialInformation() {
        return specialInformation;
    }

    public void setSpecialInformation(String specialInformation) {
        this.specialInformation = specialInformation;
    }
}
