package pl.cleankod.exchange.provider;

import com.github.benmanes.caffeine.cache.LoadingCache;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

public class CurrencyConversionCacheNbpService extends CurrencyConversionNbpService {
    private final LoadingCache<String, RateWrapper> ratesCache;

    public CurrencyConversionCacheNbpService(
            ExchangeRatesNbpClient exchangeRatesNbpClient,
            LoadingCache<String, RateWrapper> ratesCache
    ) {
        super(exchangeRatesNbpClient);
        this.ratesCache = ratesCache;

    }

    @Override
    RateWrapper fetchRate(String currencyCode) {
        return this.ratesCache.get(currencyCode);
    }


}
