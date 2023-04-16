package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.RatesCache;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final RatesCache ratesCache;
    private final CurrencyConverter currencyConverter;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                        CurrencyConverter currencyConverter,
                                        RatesCache ratesCache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.currencyConverter = currencyConverter;
        this.ratesCache = ratesCache;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        String currencyCode = targetCurrency.getCurrencyCode();
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", currencyCode);
        ratesCache.putIfAbsent("A", currencyCode, rateWrapper);
        BigDecimal midRate = rateWrapper.rates().get(0).mid();

        return currencyConverter.convert(money, targetCurrency, midRate);
    }
}
