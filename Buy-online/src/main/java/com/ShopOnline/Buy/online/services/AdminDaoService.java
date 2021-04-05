package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.Role;
import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.UserNotFoundException;
import com.ShopOnline.Buy.online.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AdminDaoService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailSenderService emailSenderService;

    @Transactional
    public String activateCustomer(Long customerId) {
        Optional<User> userOptional = userRepository.findById(customerId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            List<Role> roleList = user.getRoleList();

            Boolean checker = false;

            for(Role role : roleList) {
                if(role.getAuthority().equals("USER")) {
                    checker = true;
                }
            }

            if(checker) {
                if(user.getActive()) {
                   return "Customer is already activated";
                }
                user.setActive(true);
                userRepository.save(user);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setFrom("adarshv193@gmail.com");
                mailMessage.setSubject("Account activated");
                mailMessage.setText("Your account is activated, Thank you for choosing the Shop - online");
                emailSenderService.sendEmail(mailMessage);

                return "Customer activated";
            }
            else {
                throw new BadRequestException("The user id passed is not the customer id");
            }
        }
        else {
            throw new UserNotFoundException("Invalid user ID, No record found with id " + customerId + " in the database");
        }
    }

    @Transactional
    public String deActivateCustomer(Long customerId) {
        Optional<User> userOptional = userRepository.findById(customerId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            List<Role> roleList = user.getRoleList();

            Boolean checker = false;

            for(Role role : roleList) {
                if(role.getAuthority().equals("USER")) {
                    checker = true;
                }
            }

            if(checker) {
                if(!user.getActive()) {
                    return "Customer is already deactivated";
                }
                user.setActive(false);
                userRepository.save(user);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setFrom("adarshv193@gmail.com");
                mailMessage.setSubject("Account deactivated");
                mailMessage.setText("Your account is deactivated, Thank you for choosing the Shop - online");
                emailSenderService.sendEmail(mailMessage);

                return "Customer deactivated";
            }
            else {
                throw new BadRequestException("The user id passed is not the customer id");
            }
        }
        else {
            throw new UserNotFoundException("Invalid user ID, No record found with id " + customerId + " in the database");
        }
    }

    @Transactional
    public String activateSeller(Long sellerId) {
        Optional<User> userOptional = userRepository.findById(sellerId);
        if(userOptional.isPresent()) {

            User user = userOptional.get();
            List<Role> roleList = user.getRoleList();

            Boolean checker = false;

            for(Role role : roleList) {
                if(role.getAuthority().equals("SELLER")) {
                    checker = true;
                }
            }

            if(checker) {
                if(user.getActive()) {
                    return "Seller is already activated";
                }

                user.setActive(true);
                userRepository.save(user);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setFrom("adarshv193@gmail.com");
                mailMessage.setSubject("Account activated");
                mailMessage.setText("Your account is activated, Thank you for choosing the Shop - online");
                emailSenderService.sendEmail(mailMessage);

                return "Seller activated";
            }
            else {
                throw new BadRequestException("The user id passed is not the seller id");
            }
        }
        else {
            throw new UserNotFoundException("Invalid user ID, No record found with id " + sellerId + " in the database");
        }
    }

    @Transactional
    public String deActivateSeller(Long sellerId) {
        Optional<User> userOptional = userRepository.findById(sellerId);
        if(userOptional.isPresent()) {

            User user = userOptional.get();
            List<Role> roleList = user.getRoleList();

            Boolean checker = false;

            for(Role role : roleList) {
                if(role.getAuthority().equals("SELLER")) {
                    checker = true;
                }
            }

            if(checker) {
                if(!user.getActive()) {
                    return "Seller is already deactivated";
                }

                user.setActive(false);
                userRepository.save(user);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setFrom("adarshv193@gmail.com");
                mailMessage.setSubject("Account deactivated");
                mailMessage.setText("Your account is deactivated, Thank you for choosing the Shop - online");
                emailSenderService.sendEmail(mailMessage);

                return "Seller deactivated";
            }
            else {
                throw new BadRequestException("The user id passed is not the seller id");
            }
        }
        else {
            throw new UserNotFoundException("Invalid user ID, No record found with id " + sellerId + " in the database");
        }
    }
}
