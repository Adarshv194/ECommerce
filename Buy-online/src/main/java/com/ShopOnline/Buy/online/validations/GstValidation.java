package com.ShopOnline.Buy.online.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GstValidation implements ConstraintValidator<Gst, String> {

    private Pattern pattern;
    private Matcher matcher;

    private static final String gstPattern = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}";

    @Override
    public void initialize(Gst constraintAnnotation) { }

    private Boolean validateGst(String gst) {
        pattern = Pattern.compile(gstPattern);
        matcher = pattern.matcher(gst);
        return matcher.matches();
    }

    @Override
    public boolean isValid(String gst, ConstraintValidatorContext context) {
        if(gst == null) {
            return false;
        }

        return validateGst(gst);
    }
}
