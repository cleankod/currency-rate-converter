package pl.cleankod.exchange.provider;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.exception.SystemException;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.exchange.provider.nbp.model.RateWrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final Logger LOG = LoggerFactory.getLogger(CurrencyConversionNbpService.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Currency baseCurrency;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Currency baseCurrency) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.baseCurrency = baseCurrency;
    }

    @Override
    @Cacheable(value = { "NbpRates" })
    // For a real life scenario this Cache would need additional configurations like TTL;
    // the cache key should also include the date in order to get the correct exchange rates.
    public Money convert(Money money, Currency targetCurrency) {
        BigDecimal targetMidRate = getExchangeRate(targetCurrency);
        BigDecimal convertedAmount = money.amount().divide(targetMidRate, 2, RoundingMode.HALF_EVEN);
        return new Money(convertedAmount, targetCurrency);
    }

    private BigDecimal getExchangeRate(Currency currency) {
        try {
            RateWrapper targetRateWrapper = exchangeRatesNbpClient.fetch("A", currency.getCurrencyCode());
            return targetRateWrapper.rates().get(0).mid();
        } catch (FeignException fe) {
            LOG.error("Exception occurred when getting exchange rates. ERROR={}", fe.getMessage());
            throw new SystemException("Exception occurred while getting rates for currency=" + currency.getCurrencyCode(), fe);
        }
    }

    public Currency getBaseCurrency() {
        return this.baseCurrency;
    }
}
