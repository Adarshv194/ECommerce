package com.ShopOnline.Buy.online.security;

import com.ShopOnline.Buy.online.services.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    UserDaoService userDaoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userDaoService.loadUserByUsername(username) == null) {
            throw new UsernameNotFoundException("Invalid username entered");
        }
        else {
            return userDaoService.loadUserByUsername(username);
        }
    }
}
