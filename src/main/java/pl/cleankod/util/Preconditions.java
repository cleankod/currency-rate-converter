package pl.cleankod.util;

import java.math.BigDecimal;

public interface Preconditions {
    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("Given value cannot be null");
        }
        return obj;
    }

    static BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must be positive! " + amount);
        }
        return amount;
    }
}
