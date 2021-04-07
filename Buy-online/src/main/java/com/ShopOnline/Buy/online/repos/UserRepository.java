package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("from Customer")
    List<Customer> findAllCustomers(Pageable pageable);

    @Query("from Seller")
    List<Seller> findAllSellers(Pageable pageable);

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);
}
