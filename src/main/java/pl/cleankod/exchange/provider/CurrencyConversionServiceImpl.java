package pl.cleankod.exchange.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.domain.MoneyOperationFailedReason;
import pl.cleankod.exchange.core.gateway.CacheService;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.NbpOperationFailedReason;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.domain.Result;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static pl.cleankod.util.domain.Result.successful;

public class CurrencyConversionServiceImpl implements CurrencyConversionService {
    public static final short SCALE = 2;
    public static final String EXCHANGE_TABLE = "A";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CacheService<String, RateWrapper> cacheService;
    private final CircuitBreaker circuitBreaker;

    public CurrencyConversionServiceImpl(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                         CacheService<String, RateWrapper> cacheService, CircuitBreaker circuitBreaker) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cacheService = cacheService;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public Result<Money, MoneyOperationFailedReason> convert(Money money, Currency targetCurrency) {
        var rateWrapperResult = getRateWrapper(targetCurrency.getCurrencyCode());
        return rateWrapperResult.biMap(
                rateWrapper -> {
                    BigDecimal midRate = rateWrapper.rates().get(0).mid();
                    BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP).setScale(SCALE, RoundingMode.HALF_UP);
                    return new Money(calculatedRate, targetCurrency);
                },
                failedReason ->
                        MoneyOperationFailedReason.conversionFailedWithReason(money.currency(), targetCurrency, failedReason.getMessage()));
    }

    private Result<RateWrapper, NbpOperationFailedReason> getRateWrapper(String currencyCode) {
        LOGGER.info("Getting the rate wrapper for currency code {}", currencyCode);
        if (cacheService.contains(currencyCode)) {
            LOGGER.info("Rate wrapper for currency code {} was found in cache", currencyCode);
            return successful(cacheService.get(currencyCode));
        }

        var rateWrapperResult = getRateWrapperFromExchangeRateService(currencyCode);
        if (rateWrapperResult.isSuccessful()) {
            LOGGER.info("Rate wrapper for currency code {} is added in cache", currencyCode);
            cacheService.put(currencyCode, rateWrapperResult.successfulValue());
        }
        return rateWrapperResult;
    }

    private Result<RateWrapper, NbpOperationFailedReason> getRateWrapperFromExchangeRateService(String currencyCode) {
        LOGGER.info("Call exchange rates NBP client to get rate wrapper for currency code {}", currencyCode);
        var exchangeRateSupplier = circuitBreaker.decorateSupplier(
                () -> exchangeRatesNbpClient.fetch(EXCHANGE_TABLE, currencyCode));

        final Response response = exchangeRateSupplier.get();
        final RateWrapper rateWrapper;
        if (response.status() == HttpStatus.SC_NOT_FOUND) {
            LOGGER.warn("Rate wrapper for currency code {} not found in exchange rates NBP client", currencyCode);
            return Result.fail(NbpOperationFailedReason.getRateWrapperFailed(currencyCode));
        }
        try {
            rateWrapper = new ObjectMapper().readValue(response.body().asInputStream(), RateWrapper.class);
            return successful(rateWrapper);
        } catch (IOException e) {
            LOGGER.error("Failed converting response to RateWrapper object: {}", e.getMessage(), e);
            return Result.fail(NbpOperationFailedReason.decodeNbpServiceResponseFailed(currencyCode));
        }

    }
}
