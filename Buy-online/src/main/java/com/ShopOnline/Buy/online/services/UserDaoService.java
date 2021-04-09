package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.*;
import com.ShopOnline.Buy.online.exceptions.*;
import com.ShopOnline.Buy.online.models.*;
import com.ShopOnline.Buy.online.repos.AddressRepository;
import com.ShopOnline.Buy.online.repos.ConfirmationTokenRepository;
import com.ShopOnline.Buy.online.tokens.ConfirmationToken;
import com.ShopOnline.Buy.online.repos.RoleRepository;
import com.ShopOnline.Buy.online.repos.UserRepository;
import com.ShopOnline.Buy.online.security.GrantedAuthorityImpl;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.MappingJacksonValue;
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
    @Autowired
    AddressRepository addressRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AppUser loadUserByUsername(String username) {
        if(username != null) {
            Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                List<GrantedAuthorityImpl> authorities = new ArrayList<>();
                user.getRoleList().forEach(role -> authorities.add(new GrantedAuthorityImpl(role.getAuthority())));
                return new AppUser(user.getUserId(),user.getFirstName(),user.getUsername(),user.getPassword(),user.getEnabled(),user.getNonLocked(), user.getActive(), authorities);
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
            +"http://localhost:8080/confirm-account?token="+token.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);

            return "Registration successful, Please verify your email via Activation link sent on your registered email ID";
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
                    user.setActive(true);
                    userRepository.save(user);

                    confirmationTokenRepository.deleteConfirmationToken(confirmToken);
                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo(user.getEmail());
                    mailMessage.setFrom("adarshv193@gmail.com");
                    mailMessage.setSubject("Account activated");
                    mailMessage.setText("Your account has been activated");
                    emailSenderService.sendEmail(mailMessage);

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
                                +"http://localhost:8080/confirm-account?token="+newToken.getConfirmationToken());

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
                            +"http://localhost:8080/confirm-account?token="+token.getConfirmationToken());

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
            seller.setActive(false);

            Optional<Role> roleOptional = roleRepository.findById(3L);
            seller.setRoleList(Arrays.asList(roleOptional.get()));

            userRepository.save(seller);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(seller.getEmail());
            mailMessage.setFrom("adarshv193@gmail.com");
            mailMessage.setSubject("Registration successfull");
            mailMessage.setText("Hello seller, Thank your for choosing the shop " +
                    "Your account has been registered successfully, please wait for some time " +
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

    public Customer getLoggedInCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) authentication.getPrincipal();
        String username = appUser.getUsername();
        return (Customer) userRepository.findByUsernameIgnoreCase(username).get();
    }

    public Seller getLoggedInSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) authentication.getPrincipal();
        String username = appUser.getUsername();
        return (Seller) userRepository.findByUsernameIgnoreCase(username).get();
    }

    public MappingJacksonValue getAllCustomers(String page, String size) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),Integer.parseInt(size));

        List<Customer> allCustomers = userRepository.findAllCustomers(pageable);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userId","email","firstName","middleName","lastName",
                "active","locked","enabled","deleted");

        FilterProvider filters = new SimpleFilterProvider().addFilter("userfilter",filter);

        MappingJacksonValue mapping = new MappingJacksonValue(allCustomers);
        mapping.setFilters(filters);

        return mapping;
    }

    public MappingJacksonValue getAllSellers(String page, String size) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));

        List<Seller> allSellers = userRepository.findAllSellers(pageable);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userId","firstName","middleName","lastName",
                "email","companyName","addressSet","companyContact","active","locked","enabled");

        FilterProvider filters = new SimpleFilterProvider().addFilter("userfilter",filter);

        MappingJacksonValue mapping = new MappingJacksonValue(allSellers);
        mapping.setFilters(filters);

        return mapping;
    }

    @Transactional
    public String updateCustomerProfile(CustomerUpdateModel customerUpdateModel, Long customerId) {
        Optional<User> userOptional = userRepository.findById(customerId);
        if(userOptional.isPresent()) {
            Customer customer = (Customer) userOptional.get();

            if(customerUpdateModel.getEmail() != null) {
                throw new BadRequestException("Can't update the email of the customer, not authorized");
            }

            if(customerUpdateModel.getFirstName() != null) {
                customer.setFirstName(customerUpdateModel.getFirstName());
            }

            if(customerUpdateModel.getMiddleName() != null) {
                customer.setMiddleName(customerUpdateModel.getMiddleName());
            }

            if(customerUpdateModel.getLastName() != null) {
                customer.setLastName(customerUpdateModel.getLastName());
            }

            if(customerUpdateModel.getContact() != null) {
                if(!customerUpdateModel.getContact().matches("^[0-9]*$")) {
                    throw new BadRequestException("Phone number must contains numbers only");
                }
                else {
                    System.out.println("called");
                    if(customerUpdateModel.getContact().length() == 10) {
                        customer.setContact(customerUpdateModel.getContact());
                    }
                    else {
                        throw new BadRequestException("Phone number must contains numbers only, invalid format received");
                    }
                }
            }

            userRepository.save(customer);

            return "Profile updated successfully";
        }
        else {
            throw new UserNotFoundException("Invalid customer, Customer not found");
        }
    }

    public String updateCustomerPassword(UpdatePasswordModel updatePasswordModel, String username) {
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            String oldPassword = updatePasswordModel.getOldPassword();
            if(passwordEncoder.matches(oldPassword,user.getPassword())) {

                if(updatePasswordModel.getNewPassword().matches(updatePasswordModel.getConfirmPassword())) {
                    String newPassword = updatePasswordModel.getNewPassword();
                    user.setPassword(passwordEncoder.encode(newPassword));

                    userRepository.save(user);

                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo(user.getEmail());
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
            throw new UserNotFoundException("Invalid customer, Customer not found");
        }
    }

    @Transactional
    public String customerAddAddress(AddressModel addressModel, Long customerId) {
        Optional<User> customerOptional = userRepository.findById(customerId);
        if(customerOptional.isPresent()) {
            Customer customer = (Customer) customerOptional.get();

            ModelMapper mapper = new ModelMapper();
            Address address = mapper.map(addressModel, Address.class);

            address.setUser(customer);

            addressRepository.save(address);

            return "Address added";
        }
        else {
            throw new UserNotFoundException("Invalid customer, Customer not found");
        }
    }
    
    @Transactional
    public String customerDeleteAddress(Long addressId, Long customerId) {
        Optional<User> userOptional = userRepository.findById(customerId);
        if(userOptional.isPresent()) {
            Customer customer = (Customer) userOptional.get();

            Optional<Address> addressOptional = addressRepository.findById(addressId);
            if(addressOptional.isPresent()) {
                Address address = addressOptional.get();
                
                if(!address.getUser().getUserId().equals(customer.getUserId())) {
                    throw new BadRequestException("Address not associated with the logged in customer can't delete the address");
                }

                addressRepository.deleteAddress(customer.getUserId(),address.getAddressId());
                
                return "Address deleted with id " + addressId + " from the database";
            }
            else {
                throw new BadRequestException("Invalid address ID, No address found in the database");
            }

        }
        else {
            throw new UserNotFoundException("Invalid customer, Customer not found");
        }
    }
    
    @Transactional
    public String customerUpdateAddress(Long customerId, Long addressId, AddressUpdateModel addressUpdateModel) {
        Optional<User> userOptional = userRepository.findById(customerId);
        if(userOptional.isPresent()) {
            Customer customer = (Customer) userOptional.get();

            Optional<Address> addressOptional = addressRepository.findById(addressId);
            if(addressOptional.isPresent()) {
                Address address = addressOptional.get();
                
                if(address.getUser().getUserId().equals(customer.getUserId())) {
                    
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
                    throw new BadRequestException("Address not associated with the logged in customer, can't update the address");
                }
            }
            else {
                throw new BadRequestException("Invalid address ID, No address found in the database");
            }
        }
        else {
            throw new UserNotFoundException("Invalid customer, Customer not found");
        }
    }
}
