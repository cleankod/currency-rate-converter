package pl.cleankod.exchange.provider.util;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.MoneyOperationFailedReason;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.domain.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionStubService implements CurrencyConversionService {
    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(0.22d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(4.58d);

    @Override
    public Result<Money, MoneyOperationFailedReason> convert(Money money, Currency targetCurrency) {
        return Result.successful(money.currency().equals(targetCurrency)
                ? money
                : calculate(money, targetCurrency));
    }

    private Money calculate(Money money, Currency targetCurrency) {
        BigDecimal rate = "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
        return Money.of(money.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP), targetCurrency);
    }
}
