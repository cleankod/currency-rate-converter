package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.StubCurrencyConversionModel;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;

public class CurrencyConversionStubService implements CurrencyConversionService {
    HashMap<String, BigDecimal> stubCurrencyConversionModelMap = new HashMap<>() {
        {
            put(StubCurrencyConversionModel.of("PLN", "EUR"), BigDecimal.valueOf(0.22d));
            put(StubCurrencyConversionModel.of("EUR", "PLN"), BigDecimal.valueOf(4.58d));
        }
    };
    @Override
    public Money convert(Money money, Currency targetCurrency) {
        return money.currency().equals(targetCurrency)
                ? money
                : calculate(money, targetCurrency);
    }

    private Money calculate(Money money, Currency targetCurrency) {
        BigDecimal rate = stubCurrencyConversionModelMap.get(StubCurrencyConversionModel.of(money.currency().getCurrencyCode(), targetCurrency.getCurrencyCode()));
        return Money.of(money.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP), targetCurrency);
    }
}
