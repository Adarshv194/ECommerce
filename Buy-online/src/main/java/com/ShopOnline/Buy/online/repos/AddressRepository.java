package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.Address;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends CrudRepository<Address, Long> {

    @Modifying
    @Query(value = "delete from address where user_id=:userId and address_id=:addressId",nativeQuery = true)
    void deleteAddress(@Param("userId") Long userId, @Param("addressId") Long addressId);
}
