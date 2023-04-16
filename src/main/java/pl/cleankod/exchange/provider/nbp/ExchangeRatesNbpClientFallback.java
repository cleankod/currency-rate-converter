package pl.cleankod.exchange.provider.nbp;

import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.util.Optional;

public class ExchangeRatesNbpClientFallback implements ExchangeRatesNbpClient {

    private final RatesCache ratesCache;

    public ExchangeRatesNbpClientFallback(RatesCache ratesCache) {
        this.ratesCache = ratesCache;
    }

    @Override
    public RateWrapper fetch(String table, String currency) {
        Optional<RateWrapper> optionalRateWrapper = ratesCache.tryGet(table, currency);
        if (optionalRateWrapper.isPresent()) {
            return optionalRateWrapper.get();
        }
        throw new ExchangeRateNotAvailableException();
    }
}
