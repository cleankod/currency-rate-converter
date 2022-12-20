package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionService {

    private final ExchangeRatesProviderPort exchangeRatesProvider;

    public CurrencyConversionService(ExchangeRatesProviderPort exchangeRatesProvider) {
        this.exchangeRatesProvider = exchangeRatesProvider;
    }

    public Money convert(Money money, Currency targetCurrency) {
        BigDecimal calculatedRate =
                money.amount().divide(exchangeRatesProvider.getExchangeRate(targetCurrency),2,  RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }

}
