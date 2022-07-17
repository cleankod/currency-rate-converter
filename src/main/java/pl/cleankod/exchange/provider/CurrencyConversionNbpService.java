package pl.cleankod.exchange.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.Cache;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.provider.model.Rate;
import pl.cleankod.exchange.provider.model.RateWrapper;
import pl.cleankod.util.CurrencyConversionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionNbpService implements CurrencyConversionService {
    private final Logger log = LoggerFactory.getLogger(CurrencyConversionNbpService.class);
    private final ExchangeRatesNbpClient exchangeRatesNbpClient;
    private final Cache cache;

    public CurrencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient, Cache cache) {
        this.exchangeRatesNbpClient = exchangeRatesNbpClient;
        this.cache = cache;
    }

    @Override
    public Money convert(Money money, Currency targetCurrency, String correlationId) {
        Rate rate = this.getRateFromProviderOrCache(targetCurrency).rates().get(0);
        BigDecimal midRate = rate.mid();
        BigDecimal calculatedRate = money.amount().divide(midRate, 2, RoundingMode.HALF_UP);

        log.debug("Exchange rate of currency:{} for day: {} is : {}",targetCurrency.getCurrencyCode(),rate.effectiveDate(), rate.mid() );
        log.debug("The result of converting {} currency: {} to the target currency : {} is {} for CID: {}", money.amount(),money.currency().getCurrencyCode(),
                targetCurrency.getCurrencyCode(), calculatedRate, correlationId );

        return new Money(calculatedRate, targetCurrency);
    }

    private RateWrapper getRateFromProviderOrCache(Currency targetCurrency) {
        RateWrapper rateWrapper;
        String dateOfRequest = CurrencyConversionUtils.getPreviousWorkingDay(LocalDate.now()).toString();
        Optional<RateWrapper> optionalRateWrapper = cache.get(targetCurrency.getCurrencyCode(), dateOfRequest);
        if (optionalRateWrapper.isPresent()) {
            log.debug("Cache contains the requested target currency value for target currency : " + targetCurrency.getCurrencyCode());
            rateWrapper = optionalRateWrapper.get();
        } else {
            log.debug("Fetching the target currency value for target currency " + targetCurrency.getCurrencyCode() + " from nbp client: ");
            rateWrapper = exchangeRatesNbpClient.fetch("A", targetCurrency.getCurrencyCode(), CurrencyConversionUtils.getPreviousWorkingDay(LocalDate.now()).toString());
            cache.put(targetCurrency.getCurrencyCode(), dateOfRequest, rateWrapper);
        }
        return rateWrapper;
    }

}
