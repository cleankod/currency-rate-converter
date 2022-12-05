package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.gateway.RateFetchingService;
import pl.cleankod.exchange.provider.nbp.model.MidRate;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    public static Money of(BigDecimal amount, Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return new Money(new BigDecimal(amount), Currency.getInstance(currency));
    }

    public Money convert(RateFetchingService rateFetchingService, Currency targetCurrency) {
        MidRate midRate = rateFetchingService.fetch(targetCurrency);
        BigDecimal converted = this.amount.divide(midRate.value(), 2, RoundingMode.HALF_EVEN);
        return Money.of(converted, targetCurrency);
    }
}
