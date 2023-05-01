package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionStubService implements CurrencyConversionService {

    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(0.22d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(4.58d);

    @Override
    public BigDecimal getRate(Currency currency, Currency targetCurrency) {
        if ("PLN".equals(currency.getCurrencyCode()) && "EUR".equals(targetCurrency.getCurrencyCode())) {
            return PLN_TO_EUR_RATE;
        }
        if ("EUR".equals(currency.getCurrencyCode()) && "PLN".equals(targetCurrency.getCurrencyCode())) {
            return EUR_TO_PLN_RATE;
        }
        throw new CurrencyConversionException(currency, targetCurrency);
    }
}
