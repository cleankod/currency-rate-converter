package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.CacheManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CacheManager cacheManager;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, long cacheTimeToLiveInSeconds) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        cacheManager = CacheManager.getInstance(cacheTimeToLiveInSeconds);
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = getRateWrapper(targetCurrency);
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_EVEN);
        return new Money(calculatedRate, targetCurrency);
    }

    private RateWrapper getRateWrapper(Currency targetCurrency) {
        String cacheKey = generateCacheKey("A", targetCurrency.getCurrencyCode());
        Optional<CacheManager.CacheValue> rateWrapperOpt = askCacheForValue(cacheKey);
        return (RateWrapper) rateWrapperOpt.map(CacheManager.CacheValue::cachedObject)
                .orElseGet(() -> {
                    RateWrapper rateWrapperResponse = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode());
                    cacheManager.put(cacheKey, rateWrapperResponse);
                    return rateWrapperResponse;
                });
    }

    private Optional<CacheManager.CacheValue> askCacheForValue(String key) {
        return cacheManager.get(key);
    }

    private String generateCacheKey(String tableName, String currencyCode) {
        return tableName + "-" + currencyCode;
    }
}
