package pl.cleankod.exchange.provider;

import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    @Cacheable(
            cacheNames = "currency-cache",
            key = "#money.amount().toPlainString().concat('-').concat(#money.currency()).concat('-').concat(#targetCurrency)"
    )
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        return new Money(
                money.divide(rateWrapper.rates().get(0).mid()),
                targetCurrency
        );
    }
}
