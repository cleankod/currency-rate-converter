package pl.cleankod.exchange.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionStubService implements CurrencyConversionService {
    private final Logger log = LoggerFactory.getLogger(CurrencyConversionStubService.class);

    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(0.22d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(4.58d);

    @Override
    public Money convert(Money money, Currency targetCurrency, String correlationId) {
        log.debug("Convert money from request with corrId:" + correlationId);
        return money.currency().equals(targetCurrency)
                ? money
                : calculate(money, targetCurrency);
    }

    private Money calculate(Money money, Currency targetCurrency) {
        BigDecimal rate = "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
        return Money.of(money.amount().divide(rate,RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP), targetCurrency);
    }
}
