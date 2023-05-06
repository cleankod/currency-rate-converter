package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.ExchangeRate;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.WholeMoney;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private static Currency PLN = Currency.getInstance("PLN");
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public WholeMoney convertAndRoundToWhole(Money money, Currency targetCurrency) {
        ExchangeRate rate = getExchangeRate(targetCurrency);
        return money.convert(rate).roundToWhole();
    }

    private ExchangeRate getExchangeRate(Currency currency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", currency.getCurrencyCode());
        BigDecimal targetToPlnRate = rateWrapper.rates().get(0).mid();
        ExchangeRate rate = ExchangeRate.of(currency, PLN, targetToPlnRate).inverse();
        return rate;
    }
}
