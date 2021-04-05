package com.ShopOnline.Buy.online.models;

import com.ShopOnline.Buy.online.entities.Address;
import com.ShopOnline.Buy.online.validations.Email;
import com.ShopOnline.Buy.online.validations.Password;
import com.ShopOnline.Buy.online.validations.PasswordMatches;
import com.ShopOnline.Buy.online.validations.Phone;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Set;

@PasswordMatches
public class CustomerReigisterModel {
    @NotNull(message = "Firstname may not be null")
    private String firstName;

    private String middleName;

    @NotNull(message = "Lastname may not be null")
    private String lastName;

    @NotNull(message = "Username may not be null")
    @Column(unique = true)
    private String username;

    @Email
    @Column(unique = true)
    private String email;

    @Phone
    private String contact;

    @Password
    @NotNull(message = "Password may not be null")
    private String password;

    @NotNull(message = "Confirm password may not be null")
    private String confirmPassword;

    private Set<Address> addressSet;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Address> getAddressSet() {
        return addressSet;
    }

    public void setAddressSet(Set<Address> addressSet) {
        this.addressSet = addressSet;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
}
