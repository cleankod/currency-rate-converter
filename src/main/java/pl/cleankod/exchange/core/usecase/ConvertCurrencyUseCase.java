package pl.cleankod.exchange.core.usecase;

import java.util.Currency;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

public class ConvertCurrencyUseCase {

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public ConvertCurrencyUseCase(final CurrencyConversionService currencyConversionService, final Currency baseCurrency) {
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return  currencyConversionService.convert(money, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
