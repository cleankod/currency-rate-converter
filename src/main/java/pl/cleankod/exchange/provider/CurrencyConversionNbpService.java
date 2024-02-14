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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private static final Long DEFAULT_TIMEOUT = 5L;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConversionNbpService.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final LoadingCache<String, RateWrapper> currencyCache;
    private final Long nbpTimeoutDuration;
    private final TimeUnit nbpTimeoutUnit;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                        String currencyExpirationDuration,
                                        String currencyExpirationTimeUnit,
                                        String nbpTimeoutDuration,
                                        String nbpTimeoutUnit) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        if (currencyExpirationDuration != null && currencyExpirationTimeUnit != null) {
            caffeine.expireAfterWrite(Long.parseLong(currencyExpirationDuration), TimeUnit.valueOf(currencyExpirationTimeUnit));
        }
        currencyCache = caffeine.build(this::fetchRateWrapper);
        this.nbpTimeoutDuration = nbpTimeoutDuration != null ? Long.parseLong(nbpTimeoutDuration) : DEFAULT_TIMEOUT;
        this.nbpTimeoutUnit = nbpTimeoutUnit != null ? TimeUnit.valueOf(nbpTimeoutUnit) : DEFAULT_TIMEOUT_UNIT;
    }

    private RateWrapper fetchRateWrapper(String currencyCode) throws RateRetrievalException {
        try {
            CompletableFuture<RateWrapper> future
                    = CompletableFuture.supplyAsync(() -> exchangeRatesNbpClient.fetch("A", currencyCode));
            return future.get(nbpTimeoutDuration, nbpTimeoutUnit);
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
        BigDecimal calculatedRate = money.amount().divide(midRate, targetCurrency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
        return new Money(calculatedRate, targetCurrency);
    }
}
