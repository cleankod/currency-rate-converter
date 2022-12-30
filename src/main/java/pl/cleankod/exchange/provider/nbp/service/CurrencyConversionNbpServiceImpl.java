package pl.cleankod.exchange.provider.nbp.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.provider.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.client.ExchangeRatesNbpClient;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.exchange.provider.nbp.exception.NbpClientException;
import pl.cleankod.exchange.provider.nbp.domain.RateWrapper;
import pl.cleankod.util.Result;

import java.util.*;
import java.util.function.Function;

public class CurrencyConversionNbpServiceImpl implements CurrencyConversionService {

    private static final Logger logger = LogManager.getLogger(CurrencyConversionNbpServiceImpl.class);
    private static final String DEFAULT_TABLE = "A";

    private static final Map<String, RateWrapper> currencyTableCache = Collections.synchronizedMap(new WeakHashMap<>());

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;

    public CurrencyConversionNbpServiceImpl(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
    }

    @Override
    public Result<Money, AccountRetrievalFailedReason> convert(Money money, Currency targetCurrency) throws NbpClientException {
        return getRateWrapper(targetCurrency.getCurrencyCode())
                .map(rateWrapper -> calculate(money.amount(), rateWrapper.rates().get(0).mid()))
                .map(calculatedValue -> Result.<Money, AccountRetrievalFailedReason>successful(new Money(calculatedValue, targetCurrency)))
                .orElse(Result.fail(AccountRetrievalFailedReason.ERROR_FROM_NBP_CLIENT));
    }

    private Optional<RateWrapper> getRateWrapper(String currencyCode) throws NbpClientException {
        try {
            return Optional.of(fetchCachedValueOrNewOne(currencyCode));
        } catch (Exception ex) {
            logger.error(String.format("Nbp service error. Cannot get currency %s.", currencyCode), ex);
            return Optional.empty();
            // throw new NbpClientException(currencyCode, DEFAULT_TABLE);
        }
    }

    private RateWrapper fetchCachedValueOrNewOne(String currencyCode) {
        // TODO invalidate cache just before NBP is providing new values (probably every day at X AM) - to clarify with NBP API
        // TODO add real (not in memory cache) like redis + configuration + move to other class together with circuitBreaker
        if (currencyTableCache.containsKey(currencyCode)) {
            return currencyTableCache.get(currencyCode);
        } else {
            RateWrapper rateWrapper = circuitBreakerFetch().apply(currencyCode);
            currencyTableCache.put(currencyCode, rateWrapper);
            return rateWrapper;
        }
    }

    private Function<String, RateWrapper> circuitBreakerFetch() {
        // TODO move configuration to separate file - maybe not-standard configuration is needed
        CircuitBreaker circuitBreaker
                = CircuitBreakerRegistry.ofDefaults()
                .circuitBreaker("nbpCircuitBreaker");

        return CircuitBreaker
                .decorateFunction(circuitBreaker, currency -> exchangeRatesNbpClient.fetch(DEFAULT_TABLE, currency));
    }
}
