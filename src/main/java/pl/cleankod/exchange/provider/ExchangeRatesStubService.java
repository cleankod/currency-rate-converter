package pl.cleankod.exchange.provider;

import java.math.BigDecimal;
import java.util.Currency;

public class ExchangeRatesStubService implements ExchangeRatesService {

    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(0.22d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(4.58d);

    public BigDecimal getExchangeRate(Currency targetCurrency) {
        return "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
    }
}
