package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.User;
import com.ShopOnline.Buy.online.tokens.ConfirmationToken;
import com.ShopOnline.Buy.online.tokens.ResetPasswordToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends CrudRepository<ResetPasswordToken,Long> {

    Optional<ResetPasswordToken> findByUser(User user);

    Optional<ResetPasswordToken> findByToken(String token);
}
