package pl.cleankod.exchange.core.service;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;

public class AccountService {

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public AccountService(CurrencyConversionService currencyConversionService, Currency baseCurrency) {
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }
}
