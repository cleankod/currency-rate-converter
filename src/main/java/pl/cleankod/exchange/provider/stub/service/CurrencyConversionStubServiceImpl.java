package pl.cleankod.exchange.provider.stub.service;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.provider.CurrencyConversionService;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.util.Result;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionStubServiceImpl implements CurrencyConversionService {

    private static final String PLN_CURRENCY = "PLN";
    private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(4.58d);
    private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(0.22d);

    @Override
    public Result<Money, AccountRetrievalFailedReason> convert(Money money, Currency targetCurrency) {
        return Result.successful(money.currency().equals(targetCurrency)
                ? money
                : calculate(money, targetCurrency));
    }

    private Money calculate(Money money, Currency targetCurrency) {
        BigDecimal rate = PLN_CURRENCY.equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
        return Money.of(calculate(money.amount(), rate), targetCurrency);
    }
}
