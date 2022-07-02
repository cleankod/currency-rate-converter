package pl.cleankod.exchange.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CacheService;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionServiceImpl implements CurrencyConversionService {
    public static final short SCALE = 2;
    public static final String EXCHANGE_TABLE = "A";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CacheService<String, Optional<RateWrapper>> cacheService;
    private final CircuitBreaker circuitBreaker;

    public CurrencyConversionServiceImpl(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                         CacheService<String, Optional<RateWrapper>> cacheService, CircuitBreaker circuitBreaker) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cacheService = cacheService;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        var rateWrapperOptional = getRateWrapper(targetCurrency.getCurrencyCode());
        if (rateWrapperOptional.isEmpty()) {
            LOGGER.warn("Failed to retrieve rate wrapper from service.");
            return null;
        }
        var rateWrapper = rateWrapperOptional.get();
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP).setScale(SCALE, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }

    private Optional<RateWrapper> getRateWrapper(String currencyCode) {
        LOGGER.info("Getting the rate wrapper for currency code {}", currencyCode);
        if (cacheService.contains(currencyCode)) {
            LOGGER.info("Rate wrapper for currency code {} was found in cache", currencyCode);
            return cacheService.get(currencyCode);
        }

        var rateWrapperOptional = getRateWrapperFromExchangeRateService(currencyCode);
        LOGGER.info("Rate wrapper for currency code {} is added in cache", currencyCode);
        cacheService.put(currencyCode, rateWrapperOptional);
        return rateWrapperOptional;
    }

    private Optional<RateWrapper> getRateWrapperFromExchangeRateService(String currencyCode) {
        LOGGER.info("Call exchange rates NBP client to get rate wrapper for currency code {}", currencyCode);
        var exchangeRateSupplier = circuitBreaker.decorateSupplier(
                () -> exchangeRatesNbpClient.fetch(EXCHANGE_TABLE, currencyCode));

        final Response response = exchangeRateSupplier.get();
        final RateWrapper rateWrapper;
        if (response.status() == HttpStatus.SC_NOT_FOUND) {
            LOGGER.warn("Rate wrapper for currency code {} not found in exchange rates NBP client", currencyCode);
            return Optional.empty();
        }
        try {
            rateWrapper = new ObjectMapper().readValue(response.body().asInputStream(), RateWrapper.class);
            return Optional.of(rateWrapper);
        } catch (IOException e) {
            LOGGER.error("Failed converting response to RateWrapper object: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }
}
