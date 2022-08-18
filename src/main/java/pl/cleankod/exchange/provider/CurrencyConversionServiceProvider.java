package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.util.Preconditions;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CurrencyConversionServiceProvider implements CurrencyConversionService {

    private final Map<Currency, FixedCurrencyConversionService> currencyConvertors = new HashMap<>();

    public void addCurrencyConverter(FixedCurrencyConversionService fixedCurrencyConversionService) {
        Preconditions.requireNonNull(fixedCurrencyConversionService);
        currencyConvertors.put(fixedCurrencyConversionService.getBaseCurrency(), fixedCurrencyConversionService);
    }

    @Override
    public Optional<Money> convert(Money money, Currency targetCurrency) {
        return Optional.of(money.currency())
                .map(currencyConvertors::get)
                .flatMap(currencyConverter -> currencyConverter.convert(money, targetCurrency));
    }

}
