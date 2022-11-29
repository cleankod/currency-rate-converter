package pl.cleankod.exchange.core.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.exception.ConversionServiceNotFoundException;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;
import java.util.Map;

public class CurrencyConversionServiceSelector {
    private final Logger LOG = LoggerFactory.getLogger(CurrencyConversionServiceSelector.class);
    private final Map<Currency, CurrencyConversionService> conversionServices;

    public CurrencyConversionServiceSelector(Map<Currency, CurrencyConversionService> conversionServices) {
        this.conversionServices = conversionServices;
    }

    public CurrencyConversionService selectService(Currency currency) {
        if (conversionServices.containsKey(currency)) {
            return conversionServices.get(currency);
        }

        LOG.warn("No compatible Conversion Service available for currency={}", currency.getCurrencyCode());
        throw new ConversionServiceNotFoundException("Unable to convert from currency=" + currency.getCurrencyCode());
    }

}
