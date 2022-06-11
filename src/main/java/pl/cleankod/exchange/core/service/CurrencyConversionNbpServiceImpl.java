package pl.cleankod.exchange.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import pl.cleankod.exchange.core.cache.CacheService;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.exception.CurrencyConversionException;
import pl.cleankod.exchange.exception.ResourceNotFoundException;
import pl.cleankod.exchange.exception.error_type.CurrencyConversionErrorType;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.Rate;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;
import java.util.function.Supplier;

import static pl.cleankod.util.DateUtils.getTodayDate;

public class CurrencyConversionNbpServiceImpl implements CurrencyConversionService {

    private static final Logger LOGGER = LogManager.getLogger(CurrencyConversionNbpServiceImpl.class);
    private static final int ROUNDING_SCALE = 2;
    private static final String TABLE_A = "A";
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    private final ObjectMapper objectMapper;

    private final CacheService cacheService;

    private final CircuitBreaker countCircuitBreaker;

    private final Currency baseCurrency;

    public CurrencyConversionNbpServiceImpl(final ExchangeRatesNbpClient exchangeRatesNbpClient,
                                            final ObjectMapper objectMapper, final CacheService cacheService,
                                            final CircuitBreaker countCircuitBreaker, final Currency baseCurrency) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.objectMapper = objectMapper;
        this.cacheService = cacheService;
        this.countCircuitBreaker = countCircuitBreaker;
        this.baseCurrency = baseCurrency;
    }

    @Override
    public Money convert(final Money money, final Currency targetCurrency) {

        if (!baseCurrency.equals(targetCurrency)) {
            LOGGER.info("convert(): Calling currency conversion provider with arguments: table={}, currency={}, date={}",
                    TABLE_A, targetCurrency.getCurrencyCode(), getTodayDate());
            final RateWrapper rateWrapper = getRateWrapperAndAddToCache(targetCurrency);
            final List<Rate> rates = rateWrapper.rates();
            checkIfRatesWereObtainedFromSupplier(rates);
            final BigDecimal midRate = rates.get(0).mid();
            final BigDecimal calculatedRate = money.amount()
                    .divide(midRate, RoundingMode.HALF_UP)
                    .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP);
            return new Money(calculatedRate, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            LOGGER.error("convert(): Failed converting currency {} to {}", money.currency(), targetCurrency);
            throw new CurrencyConversionException(CurrencyConversionErrorType.CURRENCY_CONVERSION_FAIL,
                    String.format("Cannot convert currency from %s to %s.", money.currency(), targetCurrency));
        }

        return money;
    }

    private void checkIfRatesWereObtainedFromSupplier(final List<Rate> rates) {
        if (CollectionUtils.isEmpty(rates)) {
            throw new ResourceNotFoundException(CurrencyConversionErrorType.NO_RATES_FOUND, "No rates were found");
        }
    }

    private RateWrapper getRateWrapperAndAddToCache(final Currency targetCurrency) {

        final Response response = getResponseFromSupplier(targetCurrency);
        final RateWrapper rateWrapper;
        try {
            rateWrapper = objectMapper.readValue(response.body().asInputStream(), RateWrapper.class);
        } catch (final IOException ex) {
            LOGGER.error("Failed converting response to DTO object: {}", ex.getMessage(), ex);
            throw new CurrencyConversionException(CurrencyConversionErrorType.CURRENCY_CONVERSION_FAIL,
                    "Failed converting response to DTO object");
        }

        addResponseToCache(response.request().url(), rateWrapper);
        return rateWrapper;
    }

    private Response getResponseFromSupplier(final Currency targetCurrency) {
        final Supplier<Response> booksSupplier = countCircuitBreaker.decorateSupplier(() ->
                exchangeRatesNbpClient.fetch(TABLE_A, targetCurrency.getCurrencyCode(), getTodayDate()));
        try {
            return booksSupplier.get();
        } catch (final Exception ex) {
            LOGGER.error("Failed to retrieve exchange rate from supplier: {}", ex.getMessage(), ex);
            throw new CurrencyConversionException(CurrencyConversionErrorType.FAILED_TO_RETRIEVE_EXCHANGE_RATE,
                    String.format("Failed to retrieve exchange rate from supplier: %s", ex.getMessage()));
        }
    }

    private void addResponseToCache(final String url, final RateWrapper rateWrapper) {
        try {
            cacheService.add(url, rateWrapper);
        } catch (final IllegalStateException | NullPointerException ex) {
            LOGGER.error("Failed to add to cache due to: {}", ex.getMessage(), ex);
        }
    }

}
