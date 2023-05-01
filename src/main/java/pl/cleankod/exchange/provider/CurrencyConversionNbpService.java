package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public BigDecimal getRate(Currency currency, Currency targetCurrency) {
        //TODO: support conversion through PLN (e.g. EUR -> PLN -> GPB) ?
        String targetCurrencyCode = targetCurrency.getCurrencyCode();
        if (!"PLN".equals(currency.getCurrencyCode()) && "PLN".equals(targetCurrencyCode)) {
            return getRateCurrencyToPln(currency);
        }
        if ("PLN".equals(currency.getCurrencyCode()) && !"PLN".equals(targetCurrencyCode)) {
            return getRatePlnToCurrency(targetCurrency);
        }
        throw new CurrencyConversionException(currency, targetCurrency);
    }

    private BigDecimal getRatePlnToCurrency(Currency targetCurrency) {
        BigDecimal midRate = getRateCurrencyToPln(targetCurrency);
        return BigDecimal.ONE.divide(midRate, 2, RoundingMode.HALF_DOWN);
    }

    private BigDecimal getRateCurrencyToPln(Currency currency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", currency.getCurrencyCode());
        return rateWrapper.rates().get(0).mid();
    }

}
