package com.ShopOnline.Buy.online.security;

import com.ShopOnline.Buy.online.entities.AppUser;
import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.repos.UserRepository;
import com.ShopOnline.Buy.online.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userDaoService.loadUserByUsername(username) == null) {
            throw new UsernameNotFoundException("Invalid username entered");
        }
        else {
            AppUser appUser = userDaoService.loadUserByUsername(username);
            String appUsername = appUser.getUsername();
            User user = userRepository.findByUsernameIgnoreCase(appUsername).get();
/*            user.setAttempts(0);

            userRepository.save(user);*/

            return appUser;
        }
    }
}
