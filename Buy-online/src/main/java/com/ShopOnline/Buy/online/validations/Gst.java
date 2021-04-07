package com.ShopOnline.Buy.online.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GstValidation.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gst {

    String message() default "Invalid gst";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
