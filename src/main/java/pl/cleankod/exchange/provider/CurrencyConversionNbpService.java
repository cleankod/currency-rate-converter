package pl.cleankod.exchange.provider;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exceptions.RateRetrievalException;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConversionNbpService.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final LoadingCache<String, RateWrapper> currencyCache;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                        String currencyExpirationDuration,
                                        String currencyExpirationTimeUnit) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        if (currencyExpirationDuration != null && currencyExpirationTimeUnit != null) {
            caffeine.expireAfterWrite(Long.parseLong(currencyExpirationDuration), TimeUnit.valueOf(currencyExpirationTimeUnit));
        }
        currencyCache = caffeine.build(this::fetchRateWrapper);

    }

    private RateWrapper fetchRateWrapper(String currencyCode) throws RateRetrievalException {
        try {
            return exchangeRatesNbpClient.fetch("A", currencyCode);
        } catch (Exception exception) {
            LOGGER.error("Exception while fetching rates for currency " + currencyCode, exception);
            throw new RateRetrievalException(currencyCode, exception);
        }
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        RateWrapper rateWrapper = currencyCache.get(targetCurrency.getCurrencyCode());
        if (rateWrapper == null) {
            LOGGER.error("Null rate returned for currency " + targetCurrency);
            throw new RateRetrievalException("Could not retrieve rate for currency " + targetCurrency);
        }
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }
}
