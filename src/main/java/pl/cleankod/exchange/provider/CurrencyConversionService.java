package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionService {
    private final ExchangeRatesService exchangeRatesService;

    public CurrencyConversionService(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    public Money convert(Money money, Currency targetCurrency) {
        BigDecimal exchangeRate = exchangeRatesService.getExchangeRate(targetCurrency);
        return Money.convert(money.amount(), exchangeRate, targetCurrency);
    }
}
