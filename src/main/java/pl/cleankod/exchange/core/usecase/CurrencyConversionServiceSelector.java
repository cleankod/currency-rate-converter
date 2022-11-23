package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;
import java.util.Map;

public class CurrencyConversionServiceSelector {

    private final Map<Currency, CurrencyConversionService> conversionServices;
    private final CurrencyConversionService defaultCurrencyConverter;

    public CurrencyConversionServiceSelector(Map<Currency, CurrencyConversionService> conversionServices,
                                             CurrencyConversionService defaultCurrencyConverter) {
        this.conversionServices = conversionServices;
        this.defaultCurrencyConverter = defaultCurrencyConverter;
    }

    public CurrencyConversionService selectService(Currency currency) {
        if (conversionServices.containsKey(currency)) {
            return conversionServices.get(currency);
        }
        // TODO bogdan log warn unable to find conversion service for currency. Using default converter.
        return defaultCurrencyConverter;
    }

}
