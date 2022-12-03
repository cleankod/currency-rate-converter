package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.util.Currency;

public class ConvertMoneyByCurrencyUseCase {

    private final CurrencyConversionService currencyConversionService;
    private final Currency baseCurrency;

    public ConvertMoneyByCurrencyUseCase(CurrencyConversionService currencyConversionService, Currency baseCurrency) {
        this.currencyConversionService = currencyConversionService;
        this.baseCurrency = baseCurrency;
    }

    public Money convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(targetCurrency)) {
            return money.convert(currencyConversionService, targetCurrency);
        }

        if (!money.currency().equals(targetCurrency)) {
            throw new CurrencyConversionException(money.currency(), targetCurrency);
        }

        return money;
    }
}
