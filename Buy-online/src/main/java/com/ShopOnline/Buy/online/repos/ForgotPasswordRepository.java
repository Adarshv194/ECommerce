package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.tokens.ConfirmationToken;
import com.ShopOnline.Buy.online.tokens.ResetPasswordToken;
import org.springframework.data.repository.CrudRepository;

public interface ForgotPasswordRepository extends CrudRepository<ResetPasswordToken,Long> {

}
