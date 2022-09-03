package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpService;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpService exchangeRatesNbpService;

    public CurrencyConversionNbpService(ExchangeRatesNbpService exchangeRatesNbpService) {
        this.exchangeRatesNbpService = exchangeRatesNbpService;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpService.fetch("A", targetCurrency.getCurrencyCode());
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_EVEN);
        return new Money(calculatedRate, targetCurrency);
    }
}
