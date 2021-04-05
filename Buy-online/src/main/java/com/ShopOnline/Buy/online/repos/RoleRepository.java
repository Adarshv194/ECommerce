package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role,Long> {
}
