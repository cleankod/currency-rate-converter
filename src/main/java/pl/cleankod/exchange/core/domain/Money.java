package pl.cleankod.exchange.core.domain;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;


public sealed interface Money permits FractionalMoney, WholeMoney {
    static Money of(BigDecimal amount, Currency currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);

        return WholeMoney.isWhole(amount, currency) ? new WholeMoney(amount, currency) : new FractionalMoney(amount, currency);
    }

    static Money of(String amount, String currency) {
        Preconditions.requireNonNull(amount);
        Preconditions.requireNonNull(currency);
        return of(new BigDecimal(amount), Currency.getInstance(currency));
    }

    BigDecimal amount();

    Currency currency();

    default WholeMoney convertAndRoundToWhole(CurrencyConversionService currencyConverter, Currency targetCurrency) {
        return currencyConverter.convertAndRoundToWhole(this, targetCurrency);
    }
}

