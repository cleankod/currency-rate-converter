package pl.cleankod.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface MoneyRoundingUtil {

    static BigDecimal roundHalfEven(BigDecimal value) {
        Preconditions.requireNonNull(value);
        return value.setScale(3, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
    }
}
