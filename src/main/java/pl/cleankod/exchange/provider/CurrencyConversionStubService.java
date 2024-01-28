package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.model.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

// Not used - consider using it when nbp server is down;
// Add @Profile("stub") to this class and trigger the profile in application.properties when nbp server is down
public class CurrencyConversionStubService implements CurrencyConversionService {
    
    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(0.22d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(4.58d);

    @Override
    public Result<Money, String> convert(Money money, Currency targetCurrency) {
        return Result.success((money.currency().equals(targetCurrency)
                ? money
                : calculate(money, targetCurrency)));
    }

    private Money calculate(Money money, Currency targetCurrency) {
        BigDecimal rate = "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
        return Money.of(money.amount().multiply(rate).setScale(2, RoundingMode.HALF_EVEN), targetCurrency);
    }
}