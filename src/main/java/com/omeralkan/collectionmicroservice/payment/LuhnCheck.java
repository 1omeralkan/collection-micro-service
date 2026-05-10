package com.omeralkan.collectionmicroservice.payment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LuhnValidator.class)
@Documented
public @interface LuhnCheck {
    String message() default "Geçersiz kredi kartı numarası";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}