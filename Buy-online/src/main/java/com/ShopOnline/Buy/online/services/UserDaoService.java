package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.*;
import com.ShopOnline.Buy.online.exceptions.*;
import com.ShopOnline.Buy.online.models.SellerRegisterModel;
import com.ShopOnline.Buy.online.repos.ConfirmationTokenRepository;
import com.ShopOnline.Buy.online.tokens.ConfirmationToken;
import com.ShopOnline.Buy.online.models.CustomerReigisterModel;
import com.ShopOnline.Buy.online.repos.RoleRepository;
import com.ShopOnline.Buy.online.repos.UserRepository;
import com.ShopOnline.Buy.online.security.GrantedAuthorityImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserDaoService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    EmailSenderService emailSenderService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AppUser loadUserByUsername(String username) {
        if(username != null) {
            Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                List<GrantedAuthorityImpl> authorities = new ArrayList<>();
                user.getRoleList().forEach(role -> authorities.add(new GrantedAuthorityImpl(role.getAuthority())));
                return new AppUser(user.getUserId(),user.getFirstName(),user.getUsername(),user.getPassword(),user.getEnabled(),user.getNonLocked(), authorities);
            }
            else {
                throw new UserNotFoundException("User not found, Invalid username");
            }
        }
        else {
            throw new UsernameNotFoundException("Username can not be blank");
        }
    }

    public String saveNewCustomer(CustomerReigisterModel model) {
        Optional<User> existingUsernameOptional = userRepository.findByUsernameIgnoreCase(model.getUsername());
        Optional<User> existingEmailOptional = userRepository.findByEmailIgnoreCase(model.getEmail());

        if(existingEmailOptional.isPresent()) {
            return "This email id is already registered with us";
        }
        else if(existingUsernameOptional.isPresent()) {
            return "This username is already taken try something else";
        }
        else {
            ModelMapper mapper = new ModelMapper();
            Customer customer = mapper.map(model, Customer.class);

            String hPassword = passwordEncoder.encode(customer.getPassword());
            customer.setPassword(hPassword);
            customer.setDeleted(false);
            customer.setEnabled(false);
            customer.setNonLocked(true);

            Optional<Role> roleOptional = roleRepository.findById(2L);

            customer.setRoleList(Arrays.asList(roleOptional.get()));

            userRepository.save(customer);

            ConfirmationToken token = new ConfirmationToken(customer);
            confirmationTokenRepository.save(token);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(customer.getEmail());
            mailMessage.setFrom("adarshv193@gmail.com");
            mailMessage.setSubject("Complete Registration");
            mailMessage.setText("To confirm your account, Please click here : "
            +"http:localhost:8080/confirm-account?token="+token.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);

            return "Registration successfull, Please verify your email via Activation link sent on your registered email ID";
        }
    }

    @Transactional
    public String confirmCustomerAccount(String confirmToken) {
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findByConfirmationToken(confirmToken);
        if(confirmationTokenOptional.isPresent()) {
            ConfirmationToken token = confirmationTokenOptional.get();
            Date presentDate = new Date();
            if(token.getExpiryDate().getTime() - presentDate.getTime() <= 0) {
                throw new TokenExpiredException("Activation link has been expired, apply to get a new Activation Link");
            }
            else {
                Optional<User> userOptional = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
                if(userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setEnabled(true);
                    userRepository.save(user);

                    confirmationTokenRepository.deleteConfirmationToken(confirmToken);

                    return "Thank you, Your account is successfully verified enjoy shopping";
                }
                else {
                    throw new UserNotFoundException("User not found with the token provided");
                }
            }
        }
        else {
            throw new TokenNotFoundException("Error! Please provide the right confirmation token and try again");
        }
    }

    @Transactional
    public String resendActivationToken(String email) {
        Optional<User> existingEmailUserOptional = userRepository.findByEmailIgnoreCase(email);
        if(existingEmailUserOptional.isPresent()) {
            User user = existingEmailUserOptional.get();
            if(!user.getEnabled()) {
                Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findByUser(user);
                if(confirmationTokenOptional.isPresent()) {
                    ConfirmationToken token = confirmationTokenOptional.get();

                    Date presentDate = new Date();
                    if(token.getExpiryDate().getTime() - presentDate.getTime() <= 0) {
                        confirmationTokenRepository.deleteConfirmationToken(token.getConfirmationToken());

                        ConfirmationToken newToken = new ConfirmationToken(user);
                        confirmationTokenRepository.save(newToken);

                        SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setTo(user.getEmail());
                        mailMessage.setFrom("adarshv193@gmail.com");
                        mailMessage.setSubject("Complete Registration, Sending activation link again");
                        mailMessage.setText("To confirm your account, Please click here : "
                                +"http:localhost:8080/confirm-account?token="+newToken.getConfirmationToken());

                        emailSenderService.sendEmail(mailMessage);

                        return "New activation link has been sent successfully on your registered email";
                    }
                    else {
                        return "Your current activation link is not expired yet, Please use the same old link to enable your account";
                    }
                }
                else {
                    ConfirmationToken token = new ConfirmationToken(user);
                    confirmationTokenRepository.save(token);

                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo(user.getEmail());
                    mailMessage.setFrom("adarshv193@gmail.com");
                    mailMessage.setSubject("Complete Registration");
                    mailMessage.setText("To confirm your account, Please click here : "
                            +"http:localhost:8080/confirm-account?token="+token.getConfirmationToken());

                    emailSenderService.sendEmail(mailMessage);

                    return "Registration successfull, Please verify your email via Activation link sent on your registered email ID";
                }
            }
            else {
                return "No need to generate the activation link, Your account is already enabled, Try to login with your credentials";
            }
        }
        else {
            throw new UserNotFoundException("Invalid Email ID, customer is not registered");
        }
    }

    public String saveNewSeller(SellerRegisterModel sellerRegisterModel) {
        Optional<User> existingEmailUserOptional = userRepository.findByEmailIgnoreCase(sellerRegisterModel.getEmail());
        Optional<User> existingUsernameUserOptional = userRepository.findByUsernameIgnoreCase(sellerRegisterModel.getUsername());

        if(existingEmailUserOptional.isPresent()) {
            return "This email id is already registered with us";
        }
        else if(existingUsernameUserOptional.isPresent()) {
            return "This username is already taken try something else";
        }
        else {
            ModelMapper mapper = new ModelMapper();
            Seller seller = mapper.map(sellerRegisterModel, Seller.class);

            String hPass = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(hPass);
            seller.setDeleted(false);
            seller.setEnabled(false);
            seller.setNonLocked(true);

            Optional<Role> roleOptional = roleRepository.findById(3L);
            seller.setRoleList(Arrays.asList(roleOptional.get()));

            userRepository.save(seller);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(seller.getEmail());
            mailMessage.setFrom("adarshv193@gmail.com");
            mailMessage.setSubject("Registration successfull");
            mailMessage.setText("Hello seller, Thank your for choosing the shop " +
                    "Your account has been registered successfully, please wait for some time" +
                    "So that your account can be verified and enabled by our team");
            emailSenderService.sendEmail(mailMessage);

            return "Registration successfull";
        }
    }

    @Transactional
    public String enableSellerAccount(Long sellerId) {
        Optional<User> userOptianal = userRepository.findById(sellerId);
        if(userOptianal.isPresent()) {
            Boolean checRole = false;
            User user = userOptianal.get();
            for( Role checkRole : user.getRoleList()) {
                if(checkRole.getAuthority().equals("ROLE_SELLER")) {
                    checRole = true;
                }
            }
            if(checRole) {
                Seller seller = (Seller) user;
                if(!seller.getEnabled()) {
                    seller.setEnabled(true);

                    userRepository.save(seller);

                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo(seller.getEmail());
                    mailMessage.setFrom("adarshv193@gmail.com");
                    mailMessage.setSubject("Account enabled");
                    mailMessage.setText("Your account has been enabled now you can list the product");

                    emailSenderService.sendEmail(mailMessage);

                    return "Seller account enabled successfully";
                }
                else {
                    return "Seller already enabled";
                }
            }
            else {
                throw new BadRequestException("Entered id is not a Seller ID");
            }
        }
        else {
            throw new UserNotFoundException("Seller ID not found");
        }
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) authentication.getPrincipal();
        String username = appUser.getUsername();
        return userRepository.findByUsernameIgnoreCase(username).get();
    }

    public Seller getLoggedInSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) authentication.getPrincipal();
        String username = appUser.getUsername();
        return (Seller) userRepository.findByUsernameIgnoreCase(username).get();
    }
}
