package pl.cleankod.exchange.provider.nbp.service;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.entrypoint.model.ErrorResponse;
import pl.cleankod.exchange.entrypoint.util.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionStubService implements CurrencyConversionService {
    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(0.22d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(4.58d);

    @Override
    public Result<Money, ErrorResponse> convert(Money money, Currency targetCurrency) {
        return money.currency().equals(targetCurrency)
                ? Result.successful(money)
                : calculate(money, targetCurrency);
    }

    private Result<Money, ErrorResponse> calculate(Money money, Currency targetCurrency) {
        BigDecimal rate = "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
        return Result.successful(Money.of(money.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP), targetCurrency));
    }
}
