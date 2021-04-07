package com.ShopOnline.Buy.online.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    AppUserDetailsService userDetailsService;

    public ResourceServerConfiguration() {
        super();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").anonymous()
                .antMatchers("/customer-registration").anonymous()
                .antMatchers("/confirm-account").anonymous()
                .antMatchers("/request-actToken").anonymous()
                .antMatchers("/seller-registration").anonymous()
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/seller/**").hasAnyRole("SELLER")
                .antMatchers("/customer/**").hasAnyRole("USER")
                .antMatchers("/add-parent-category").hasAnyRole("ADMIN")
                .antMatchers("/add-category/{parentCategory}").hasAnyRole("ADMIN")
                .antMatchers("/get-all-categories").hasAnyRole("ADMIN","SELLER")
                .antMatchers("/get-category/{categoryId}").hasAnyRole("ADMIN","SELLER")
                .antMatchers("/update-category/{categoryName}").hasAnyRole("ADMIN")
                .antMatchers("/add-metadata/fields").hasAnyRole("ADMIN")
                .antMatchers("/all-metadata-fileds").hasAnyRole("ADMIN","SELLER")
                .antMatchers("/update-metadata-field/{fieldId}/{fieldName}").hasAnyRole("ADMIN")
                .antMatchers("/add-metadata/field/values/{categoryId}/{metadataFieldId}").hasAnyRole("ADMIN")
                .antMatchers("/update-metadata/field/values/{categoryId}/{metadataFieldId}").hasAnyRole("ADMIN")
                .antMatchers("/get-metadata-field-values/").hasAnyRole("ADMIN","SELLER")
                .antMatchers("/customer/get-all-categories").hasAnyRole("USER","ADMIN")
                .antMatchers("/customer/get-all-categories/{categoryId}").hasAnyRole("USER","ADMIN")
                .antMatchers("/customer/get-category/{categoryId}").hasAnyRole("ADMIN","USER")
                .antMatchers("/add-product/{categoryName}").hasAnyRole("SELLER","ADMIN")
                .antMatchers("/add-product-variation/{productId}").hasAnyRole("SELLER","ADMIN")
                .antMatchers("/update-product/{productId}").hasAnyRole("SELLER","ADMIN")
                .antMatchers("/update-product-variation/{productVariationId}").hasAnyRole("SELLER","ADMIN")
                .antMatchers("/admin/activate-product/{productId}").hasAnyRole("ADMIN")
                .antMatchers("/admin/deactivate-product/{productId}").hasAnyRole("ADMIN")
                .antMatchers("/user-account-unlock/{username}").anonymous()
                .antMatchers("/do-unlock").anonymous()
                .antMatchers("/doLogout").hasAnyRole("ADMIN","SELLER","USER")
                .antMatchers("/forgot-password").anonymous()
                .antMatchers("/reset-password").anonymous()
                .antMatchers("/customer-home-profile").hasAnyRole("USER")
                .antMatchers("/customer-home-profile-address").hasAnyRole("USER")
                .antMatchers("/customer-profile-update").hasAnyRole("USER")
                .antMatchers("/customer-update-password").hasAnyRole("USER")
                .antMatchers("/customer/add-address").hasAnyRole("USER")
                .antMatchers("/customer/delete-address/{addressId}").hasAnyRole("USER")
                .antMatchers("/customer/update-address/addressId}").hasAnyRole("USER")
                .antMatchers("/seller-home-profile").hasAnyRole("SELLER")
                .antMatchers("/seller-profile-update").hasAnyRole("SELLER")
                .antMatchers("/seller-update-password").hasAnyRole("SELLER")
                .antMatchers("/seller-update-address/{addressId}").hasAnyRole("SELLER")

                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable();
    }
}
