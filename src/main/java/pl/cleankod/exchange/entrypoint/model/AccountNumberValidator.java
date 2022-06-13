package pl.cleankod.exchange.entrypoint.model;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class AccountNumberValidator implements ConstraintValidator<AccountNumber, AccountNumberDto> {

    private static final Pattern PATTERN =
            Pattern.compile("\\d{2}[+]?\\d{4}[+]?\\d{4}[+]?\\d{4}[+]?\\d{4}[+]?\\d{4}[+]?\\d{4}");

    @Override
    public void initialize(AccountNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AccountNumberDto value, ConstraintValidatorContext context) {
        return PATTERN.matcher(value.number()).matches();
    }
}
