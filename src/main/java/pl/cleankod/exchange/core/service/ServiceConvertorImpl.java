package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CircuitBreaker;
import pl.cleankod.exchange.core.gateway.ServiceConvertor;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;

import java.util.Currency;

public class ServiceConvertorImpl implements ServiceConvertor {

    private final CircuitBreaker  circuitBreaker;
    private final Currency baseCurrency;

    public ServiceConvertorImpl(CircuitBreaker circuitBreaker,  Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
        this.circuitBreaker = circuitBreaker;
    }

    public Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return circuitBreaker.convert(money, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }

}
