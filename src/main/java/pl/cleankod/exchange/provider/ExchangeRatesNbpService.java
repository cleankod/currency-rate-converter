package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.util.Currency;

public class ExchangeRatesNbpService implements ExchangeRatesService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public ExchangeRatesNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    public BigDecimal getExchangeRate(Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        return rateWrapper.rates().get(0).mid();
    }
}
