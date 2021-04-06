package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.TokenExpiredException;
import com.ShopOnline.Buy.online.exceptions.TokenNotFoundException;
import com.ShopOnline.Buy.online.exceptions.UserNotFoundException;
import com.ShopOnline.Buy.online.models.ForgotPasswordModel;
import com.ShopOnline.Buy.online.repos.ForgotPasswordRepository;
import com.ShopOnline.Buy.online.repos.UserRepository;
import com.ShopOnline.Buy.online.tokens.ResetPasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ForgotPasswordDaoService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ForgotPasswordRepository forgotPasswordRepository;
    @Autowired
    EmailSenderService emailSenderService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            if(!user.getActive()) {
                throw new BadRequestException("Can't update the password as the user is not activated");
            }

            Optional<ResetPasswordToken> resetPasswordTokenOptional = forgotPasswordRepository.findByUser(user);
            ResetPasswordToken resetPasswordToken = null;

            if(resetPasswordTokenOptional.isPresent()) {
                resetPasswordToken = resetPasswordTokenOptional.get();
                resetPasswordToken.calculate();

                forgotPasswordRepository.save(resetPasswordToken);
            }
            else {
                resetPasswordToken = new ResetPasswordToken(user);
                forgotPasswordRepository.save(resetPasswordToken);
            }

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setFrom("adarshv193@gmail.com");
                mailMessage.setSubject("Reset your password");
                mailMessage.setText("To reset your password, Please click here : "
                +"http://localhost:8080/reset-password?token="+resetPasswordToken.getToken());
                emailSenderService.sendEmail(mailMessage);

                return "A reset-password link has been sent to your registered email";
            }
        else {
            throw new UserNotFoundException("Invalid user email id, No user found with the email " + email + " ");
        }
    }

    public String resetPassword(String token, ForgotPasswordModel forgotPasswordModel) {
        Optional<ResetPasswordToken> resetPasswordTokenOptional = forgotPasswordRepository.findByToken(token);
        if(resetPasswordTokenOptional.isPresent()) {
            ResetPasswordToken resetPasswordToken = resetPasswordTokenOptional.get();

            Date presentDate = new Date();
            if(resetPasswordToken.getExpiryDate().getTime() - presentDate.getTime() <= 0) {
                forgotPasswordRepository.delete(resetPasswordToken);
                throw new TokenExpiredException("Token has been expired, request for new password through forgot-password link");
            }
            else {
                Optional<User> userOptional = userRepository.findByEmailIgnoreCase(resetPasswordToken.getUser().getEmail());
                if(userOptional.isPresent()) {
                    User user = userOptional.get();

                    if(forgotPasswordModel.getPassword().equals(forgotPasswordModel.getConfirmPassword())) {
                        String hPass = passwordEncoder.encode(forgotPasswordModel.getPassword());
                        user.setPassword(hPass);

                        userRepository.save(user);

                        SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setTo(user.getEmail());
                        mailMessage.setFrom("adarshv193@gmail.com");
                        mailMessage.setSubject("Password Updated");
                        mailMessage.setText("Your password has been updated, use your new password to log in");
                        emailSenderService.sendEmail(mailMessage);

                        return "Password updated successfully";
                    }
                    else {
                        throw new BadRequestException("Password and confirm password does not matches");
                    }
                }
                else {
                    throw new UserNotFoundException("User not found with the token provided");
                }
            }
        }
        else {
            throw new TokenNotFoundException("Invalid token, Please provide a valid token");
        }
    }
}
