package pl.cleankod.exchange.entrypoint.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface AccountNumber {

    String message() default "The account number does not match the pattern!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
