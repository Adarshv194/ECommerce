package com.ShopOnline.Buy.online.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Role {
    @Id
    private Long roleId;
    private String authority;

    @ManyToMany(mappedBy = "roleList")
    private List<User> userList;

    public Role() { }

    public Role(Long roleId, String authority) {
        this.roleId = roleId;
        this.authority = authority;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
