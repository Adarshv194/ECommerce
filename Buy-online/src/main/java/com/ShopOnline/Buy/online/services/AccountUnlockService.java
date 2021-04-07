package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.exceptions.UserNotFoundException;
import com.ShopOnline.Buy.online.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class AccountUnlockService {

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    UserRepository userRepository;

    public String unlockAccount(String username) {
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setFrom("adarshv193@gmail.com");
            mailMessage.setSubject("Unlock your account");
            mailMessage.setText("To unlock your account , please click here :"
                    +"http://localhost:8080/do-unlock?username="+username);

            emailSenderService.sendEmail(mailMessage);

            return "Mail has been sent to your registered email, Click on the link to unlock your account";
        }
        else {
            throw new UserNotFoundException("Invalid username, User not found with the username " + username + " ");
        }
    }

    @Transactional
    public String unlockAccountSuccess(String username) {
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            user.setAttempts(0);
            user.setNonLocked(true);

            userRepository.save(user);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setFrom("adarshv193@gmail.com");
            mailMessage.setSubject("Account unlocked");
            mailMessage.setText("your account has been unlocked successfully");
            emailSenderService.sendEmail(mailMessage);

            return "Account is unlock now";
        }
        else {
            throw new UserNotFoundException("Invalid username, User not found with the username " + username + " ");
        }
    }
}
