package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.tokens.ConfirmationToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken,Long> {

    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);

    Optional<ConfirmationToken> findByUser(User user);

    @Modifying
    @Query(value = "delete from confirmation_token where confirmation_token=:token",nativeQuery = true)
    void deleteConfirmationToken(@Param("token") String token);
}
