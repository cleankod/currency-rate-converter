package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.gateway.RateFetchingService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.MidRate;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Currency;

public class RateFetchingNbpService implements RateFetchingService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public RateFetchingNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public MidRate fetch(Currency targetCurrency) {
        RateWrapper rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
        return new MidRate(rateWrapper.rates().get(0).mid());
    }
}
