package com.ShopOnline.Buy.online.security;

import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.exceptions.UserNotFoundException;
import com.ShopOnline.Buy.online.repos.UserRepository;
import com.ShopOnline.Buy.online.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LockAuthenticationManager implements ApplicationListener<AbstractAuthenticationEvent> {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailSenderService emailSenderService;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent appEvent) {
        if(appEvent instanceof AuthenticationSuccessEvent) {
            AuthenticationSuccessEvent successEvent = (AuthenticationSuccessEvent) appEvent;
        }

        if(appEvent instanceof AuthenticationFailureBadCredentialsEvent) {
            AuthenticationFailureBadCredentialsEvent failureEvent = (AuthenticationFailureBadCredentialsEvent) appEvent;

            String username = (String) failureEvent.getAuthentication().getPrincipal();
            Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
            if(userOptional.isPresent()) {
                User user = userOptional.get();

                if(user.getAttempts() >= 2) {
                    user.setNonLocked(false);

                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo(user.getEmail());
                    mailMessage.setFrom("adarshv193@gmail.com");
                    mailMessage.setSubject("Account Locked");
                    mailMessage.setText("Your account has been locked due to multiple incorrect password attempts " +
                            "Go to this link to unlock your account"
                            +"http://localhost:8080/user-account-unlock/{username}");
                    emailSenderService.sendEmail(mailMessage);
                }
                else {
                    user.setAttempts(user.getAttempts() + 1);
                }

                userRepository.save(user);
            }
            else {
                throw new UserNotFoundException("Invlaid username, No user found");
            }
        }
    }
}
