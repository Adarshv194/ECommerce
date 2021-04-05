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
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable();
    }
}
