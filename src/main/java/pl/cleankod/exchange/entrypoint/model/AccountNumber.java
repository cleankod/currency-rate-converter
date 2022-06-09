package pl.cleankod.exchange.entrypoint.model;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface AccountNumber {

    String message = "The account number does not match the pattern: ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class AccountNumberValidator implements ConstraintValidator<AccountNumber, String> {

    private static final Pattern PATTERN =
            Pattern.compile("\\d{2}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PATTERN.matcher(value).matches();
    }
}
