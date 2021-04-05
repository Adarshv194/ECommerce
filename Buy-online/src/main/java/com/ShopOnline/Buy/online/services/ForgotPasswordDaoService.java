package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.UserNotFoundException;
import com.ShopOnline.Buy.online.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordDaoService {

    @Autowired
    UserRepository userRepository;

    public String forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            if(!user.getActive()) {
                throw new BadRequestException("Can't update the password as the user is not activated");
            }

            
        }
        else {
            throw new UserNotFoundException("Invalid user email id, No user found with the email " + email + " ");
        }
    }
}
