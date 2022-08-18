package pl.cleankod.exchange.provider.conversion.nbp;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.provider.conversion.FixedCurrencyConversionService;
import pl.cleankod.exchange.provider.conversion.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.conversion.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService extends FixedCurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Currency baseCurrency) {
        super(baseCurrency);
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Money doConvert(BigDecimal amount, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        return Money.of(amount, midRate, targetCurrency);
    }
}
