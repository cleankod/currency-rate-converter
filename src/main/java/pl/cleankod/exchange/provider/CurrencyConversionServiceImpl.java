package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CacheService;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionServiceImpl implements CurrencyConversionService {
    public static final short SCALE = 2;
    public static final String EXCHANGE_TABLE = "A";

    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final CacheService<String, Optional<RateWrapper>> cacheService;

    public CurrencyConversionServiceImpl(ExchangeRatesNbpClient exchangeRatesNbpClient,
                                         CacheService<String, Optional<RateWrapper>> cacheService) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cacheService = cacheService;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency) {
        var rateWrapperOptional = getRateWrapper(targetCurrency.getCurrencyCode());
        if (rateWrapperOptional.isEmpty()) {
            return null;
        }
        var rateWrapper = rateWrapperOptional.get();
        BigDecimal midRate = rateWrapper.rates().get(0).mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, RoundingMode.HALF_UP).setScale(SCALE, RoundingMode.HALF_UP);
        return new Money(calculatedRate, targetCurrency);
    }

    private Optional<RateWrapper> getRateWrapper(String currencyCode) {
        if (cacheService.contains(currencyCode)) {
            return cacheService.get(currencyCode);
        }

        var rateWrapper = exchangeRatesNbpClient.fetch(EXCHANGE_TABLE, currencyCode);
        cacheService.put(currencyCode, Optional.of(rateWrapper));
        return Optional.of(rateWrapper);
    }
}
