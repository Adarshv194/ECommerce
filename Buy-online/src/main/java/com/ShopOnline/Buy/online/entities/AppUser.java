package com.ShopOnline.Buy.online.entities;

import com.ShopOnline.Buy.online.security.GrantedAuthorityImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUser implements UserDetails {
    private Long userId;
    private String name;
    private String username;
    private String password;
    private Boolean isEnabled;
    private Boolean isNonLocked;
    private Boolean isActive;
    private List<GrantedAuthorityImpl> grantedAuthorities;

    public AppUser(Long userId, String name, String username, String password, Boolean isEnabled, Boolean isNonLocked, Boolean isActive, List<GrantedAuthorityImpl> grantedAuthorities) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.isEnabled = isEnabled;
        this.isNonLocked = isNonLocked;
        this.grantedAuthorities = grantedAuthorities;
        this.isActive = isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
