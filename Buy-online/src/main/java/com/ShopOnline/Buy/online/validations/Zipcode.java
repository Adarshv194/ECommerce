package com.ShopOnline.Buy.online.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ZipcodeValidation.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Zipcode {

    String message() default "Invalid Zipcode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
