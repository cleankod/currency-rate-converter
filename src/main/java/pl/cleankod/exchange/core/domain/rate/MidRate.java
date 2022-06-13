package pl.cleankod.exchange.core.domain.rate;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;

public record MidRate(Currency currency, BigDecimal rate) {

    public MidRate(String currency, BigDecimal rate) {
        this(Currency.getInstance(currency), rate);
    }

    public MidRate {
        Preconditions.requireNonNull(rate);
        Preconditions.requireNonNull(currency);
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MidRateCanNotBeLowerThenZeroException("Mid rate can not be less then 0!");
        }
    }
}
