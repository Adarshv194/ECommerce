package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.entities.Address;
import com.ShopOnline.Buy.online.validations.Email;
import com.ShopOnline.Buy.online.validations.Password;
import com.ShopOnline.Buy.online.validations.PasswordMatches;
import com.ShopOnline.Buy.online.validations.Phone;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@PasswordMatches
public class SellerRegisterModel {
    @NotNull
    private String firstName;

    private String middleName;

    @NotNull
    private String lastName;

    @NotNull
    @Column(unique = true)
    private String username;

    @Email
    @Column(unique = true)
    private String email;

    @Size(max = 1, message = "Only one company address is allowed")
    @NotNull
    private Set<Address> addressSet;

    @NotNull
    @Password
    private String password;

    @NotNull
    private String confirmPassword;

    private String gst;

    @NotNull
    @Column(unique = true)
    private String companyName;

    @Phone
    private String companyContact;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Address> getAddressSet() {
        return addressSet;
    }

    public void setAddressSet(Set<Address> addressSet) {
        this.addressSet = addressSet;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }
}
