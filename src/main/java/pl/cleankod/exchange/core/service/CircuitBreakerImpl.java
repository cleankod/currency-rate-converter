package pl.cleankod.exchange.core.service;

import feign.FeignException;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CircuitBreaker;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.CurrencyConversionStubService;

import java.util.Currency;

public class CircuitBreakerImpl implements CircuitBreaker {

    private final CurrencyConversionService currencyConversionNbpService;
    private final CurrencyConversionService currencyConversionStubService;

    public CircuitBreakerImpl(CurrencyConversionService currencyConversionNbpService) {
        this.currencyConversionNbpService = currencyConversionNbpService;
        this.currencyConversionStubService = new CurrencyConversionStubService();
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        Money moneyConverted;

        try {
            moneyConverted = money.convert(currencyConversionNbpService, targetCurrency);
        } catch (FeignException ex) {
            moneyConverted = money.convert(currencyConversionStubService, targetCurrency);
        }

        return  moneyConverted;
    }
}
