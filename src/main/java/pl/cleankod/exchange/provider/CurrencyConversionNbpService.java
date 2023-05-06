package pl.cleankod.exchange.provider;

import io.github.resilience4j.cache.Cache;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpCache;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;
import pl.cleankod.util.domain.AppErrors;
import pl.cleankod.util.domain.Either;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Cache<ExchangeRatesNbpCache.Key, RateWrapper> exchangeRatesNbpCache;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache<ExchangeRatesNbpCache.Key, RateWrapper> exchangeRatesNbpCache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.exchangeRatesNbpCache = exchangeRatesNbpCache;
    }

    @Override
    public Either<? extends AppErrors.Base, BigDecimal> getRate(Currency currency, Currency targetCurrency) {
        //TODO: support conversion through PLN (e.g. EUR -> PLN -> GPB) ?
        String targetCurrencyCode = targetCurrency.getCurrencyCode();
        if ("PLN".equals(currency.getCurrencyCode()) && !"PLN".equals(targetCurrencyCode)) {
            return getRatePlnToCurrency(targetCurrency);
        } else {
            return AppErrors.eitherConversion(currency, targetCurrency);
        }
    }

    private Either<AppErrors.Provider, BigDecimal> getRatePlnToCurrency(Currency targetCurrency) {
        return getRateCurrencyToPln(targetCurrency)
                .mapRight(rate -> BigDecimal.ONE.divide(rate, 2, RoundingMode.HALF_DOWN));
    }

    private Either<AppErrors.Provider, BigDecimal> getRateCurrencyToPln(Currency currency) {
        return Either.tryCatch(() -> fetchCached(currency.getCurrencyCode()))
                .mapBi(
                        throwable -> AppErrors.provider(throwable.getMessage()),
                        rateWrapper -> rateWrapper.rates().get(0).mid()
                );
    }

    private RateWrapper fetchCached(String currencyCode) {
        return Cache.decorateSupplier(
                        exchangeRatesNbpCache,
                        () -> exchangeRatesNbpClient.fetch("A", currencyCode))
                .apply(new ExchangeRatesNbpCache.Key("A", currencyCode));
    }

}
