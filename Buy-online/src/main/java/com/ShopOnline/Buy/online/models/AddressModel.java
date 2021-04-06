package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.validations.Zipcode;

import javax.validation.constraints.NotNull;

public class AddressModel {
    @NotNull
    private String country;

    @NotNull
    private String state;

    @NotNull
    private String city;

    @NotNull
    private String addressLine;

    @Zipcode
    private String zipCode;

    @NotNull
    private String label;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
