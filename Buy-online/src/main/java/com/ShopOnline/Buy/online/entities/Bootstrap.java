package com.ShopOnline.Buy.online.entities;

import com.ShopOnline.Buy.online.repos.RoleRepository;
import com.ShopOnline.Buy.online.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
/*        Role admin = new Role();
        admin.setRoleId(1l);
        admin.setAuthority("ROLE_ADMIN");
        roleRepository.save(admin);

        User user = new User();
        user.setFirstName("Adarsh");
        user.setLastName("Verma");
        user.setUsername("adarshAdmin");
        user.setEmail("adarsh.verma@tothenew.com");
        user.setPassword(passwordEncoder.encode("adminPass"));
        user.setActive(true);
        user.setNonLocked(true);
        user.setEnabled(true);
        user.setDeleted(false);

        user.setRoleList(Arrays.asList(admin));

        userRepository.save(user);

        Role customer = new Role(2L,"ROLE_USER");
        Role seller = new Role(3L,"ROLE_SELLER");

        roleRepository.save(customer);
        roleRepository.save(seller);
  */  }
}
