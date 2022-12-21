package pl.cleankod.exchange.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exception.CurrencyConversionUnsupportedException;
import pl.cleankod.exchange.core.exception.ExchangeRateNotApplicableException;
import pl.cleankod.exchange.core.exception.UnavailableExchangeRateException;
import pl.cleankod.exchange.core.port.ExchangeRatesProviderPort;
import pl.cleankod.exchange.util.Failure;
import pl.cleankod.exchange.util.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionService {

    private final ExchangeRatesProviderPort exchangeRatesProvider;
    private final Currency baseCurrency;
    private static final Logger log = LoggerFactory.getLogger(CurrencyConversionService.class);


    public CurrencyConversionService(ExchangeRatesProviderPort exchangeRatesProvider, Currency baseCurrency) {
        this.exchangeRatesProvider = exchangeRatesProvider;
        this.baseCurrency = baseCurrency;
    }

    public Money convert(Money money, Currency targetCurrency) {

        // this solves a bug because without this clause here and in the case where
        // money.currency() = EUR and targetCurrency = EUR and  baseCurrency = PLN (hence != targetCurrency = EUR)
        // the original euros get wrongly converted to PLN  --> should be covered with test case
        if (money.currency().equals(targetCurrency)){  //no conversion
            return money;
        }

        if (baseCurrency.equals(targetCurrency)) {
            // Case of money.currency() !=  targetCurrency = baseCurrency
            throw new CurrencyConversionUnsupportedException(money.currency(), targetCurrency);
        }


        // Case of money.currency() !=  targetCurrency != baseCurrency: conversion should be performed
        return performConversion(money, targetCurrency);

    }

    private Money performConversion(Money money, Currency targetCurrency) {
        Result<BigDecimal, Failure> result = exchangeRatesProvider.getExchangeRate(targetCurrency);

        if (result.isFail()){
            log.error(result.failValue().failureReason.toString() + " " + result.failValue().exception.getMessage()); // can be improved
            // can be customized, for instance, to return the account balance with the original currency
            // and an additional end-user message stating that the conversion couldn't be performed
            throw new UnavailableExchangeRateException(money.currency(), targetCurrency);
        }

        if (result.successfulValue() == null || result.successfulValue().doubleValue() <= 0d){
            log.error("Exchange rate does not have a permissible value: " + result.successfulValue());
            throw new ExchangeRateNotApplicableException(result.successfulValue());
        }
        BigDecimal calculatedRate =
                money.amount().divide(result.successfulValue(),2,  RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);

    }

}
