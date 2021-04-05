package com.ShopOnline.Buy.online.security;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }

    public GrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

}
