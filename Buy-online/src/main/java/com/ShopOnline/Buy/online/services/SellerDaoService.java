package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.Address;
import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.UserNotFoundException;
import com.ShopOnline.Buy.online.models.AddressUpdateModel;
import com.ShopOnline.Buy.online.models.SellerUpdateModel;
import com.ShopOnline.Buy.online.models.UpdatePasswordModel;
import com.ShopOnline.Buy.online.repos.AddressRepository;
import com.ShopOnline.Buy.online.repos.UserRepository;
import com.ShopOnline.Buy.online.validations.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class SellerDaoService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    EmailSenderService emailSenderService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public String updateSellerProfile(SellerUpdateModel sellerUpdateModel, Long sellerId) {
        Optional<User> userOptional = userRepository.findById(sellerId);
        if(userOptional.isPresent()) {
            Seller seller = (Seller) userOptional.get();

            if(sellerUpdateModel.getEmail() != null) {
                throw new BadRequestException("Can't update the seller email id, not authorized");
            }

            if(sellerUpdateModel.getFirstName() != null) {
                seller.setFirstName(sellerUpdateModel.getFirstName());
            }

            if(sellerUpdateModel.getMiddleName() != null) {
                seller.setMiddleName(sellerUpdateModel.getMiddleName());
            }

            if(sellerUpdateModel.getLastName() != null) {
                seller.setLastName(sellerUpdateModel.getLastName());
            }

            if(sellerUpdateModel.getCompanyName() != null) {
                seller.setCompanyName(sellerUpdateModel.getCompanyName());
            }

            if(sellerUpdateModel.getCompanyContact() != null) {
                if(sellerUpdateModel.getCompanyContact().matches("^[0-9]*$")) {
                    seller.setCompanyContact(sellerUpdateModel.getCompanyContact());
                }
                else {
                    throw new BadRequestException("Company contact should only contains numbers");
                }
            }

            if(sellerUpdateModel.getGst() != null) {
                if(sellerUpdateModel.getGst().matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}")) {
                    seller.setGst(sellerUpdateModel.getGst());
                }
                else {
                    throw new BadRequestException("Gst invlaid format, please provide the gst with right format");
                }
            }

            userRepository.save(seller);

            return "Profile update successfully";
        }
        else {
            throw new UserNotFoundException("Invalid seller, No seller found");
        }
    }

    @Transactional
    public String updateSellerPassword(Long sellerId, UpdatePasswordModel updatePasswordModel) {
        Optional<User> userOptional = userRepository.findById(sellerId);
        if(userOptional.isPresent()) {
            Seller seller = (Seller) userOptional.get();

            String oldPassword = updatePasswordModel.getOldPassword();
            if(passwordEncoder.matches(oldPassword,seller.getPassword())) {
                if(updatePasswordModel.getNewPassword().matches(updatePasswordModel.getConfirmPassword())) {
                    String newPassword = updatePasswordModel.getNewPassword();

                    seller.setPassword(passwordEncoder.encode(newPassword));

                    userRepository.save(seller);

                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo(seller.getEmail());
                    mailMessage.setFrom("adarshv193@gmail.com");
                    mailMessage.setSubject("Password updated");
                    mailMessage.setText("Your password has been changed, if it's not done by you kindly contact the administration");

                    emailSenderService.sendEmail(mailMessage);

                    return "Password updated successfully";
                }
                else {
                    throw new BadRequestException("New password and confirm password does not matches");
                }
            }
            else {
                throw new BadRequestException("Old password does not matches with your credentials saved in our database, Please try again with correct credentials or reset your password through forgot-password");
            }
        }
        else {
            throw new UserNotFoundException("Invalid seller, No seller found");
        }
    }

    @Transactional
    public String updateSellerAddress(Long addressId, Long sellerId, AddressUpdateModel addressUpdateModel) {
        Optional<User> userOptional = userRepository.findById(sellerId);
        if(userOptional.isPresent()) {
            Seller seller = (Seller) userOptional.get();

            Optional<Address> addressOptional = addressRepository.findById(addressId);
            if(addressOptional.isPresent()) {
                Address address = addressOptional.get();

                if(address.getUser().getUserId().equals(seller.getUserId())) {

                    if(addressUpdateModel.getAddressLine() != null) {
                        address.setAddressLine(addressUpdateModel.getAddressLine());
                    }

                    if(addressUpdateModel.getCountry() != null) {
                        address.setCountry(addressUpdateModel.getCountry());
                    }

                    if(addressUpdateModel.getState() != null) {
                        address.setState(addressUpdateModel.getState());
                    }

                    if(addressUpdateModel.getCity() != null) {
                        address.setCity(addressUpdateModel.getCity());
                    }

                    if(addressUpdateModel.getZipCode() != null) {
                        address.setZipCode(addressUpdateModel.getZipCode());
                    }

                    if(addressUpdateModel.getLabel() != null) {
                        address.setLabel(addressUpdateModel.getLabel());
                    }

                    addressRepository.save(address);

                    return "Address updated with id " + addressId + " from the database";
                }
                else {
                    throw new BadRequestException("Address not associated with the logged in seller, can't update the address");
                }
            }
            else {
                throw new BadRequestException("Invalid address ID, No address found in the database");
            }
        }
        else {
            throw new UserNotFoundException("Invalid seller, No seller found");
        }
    }
}
