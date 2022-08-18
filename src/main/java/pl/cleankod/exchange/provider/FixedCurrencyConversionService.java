package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * A conversion service which will always convert from a base currency to the target currency.
 */
public abstract class FixedCurrencyConversionService implements CurrencyConversionService {

    private final Currency baseCurrency;

    public FixedCurrencyConversionService(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    protected Currency getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * @return empty if the money are expressed in a different currency than the base currency.
     */
    public Optional<Money> convert(Money money, Currency targetCurrency) {
        if (!baseCurrency.equals(money.currency())) {
            return Optional.empty();
        }

        return Optional.of(doConvert(money.amount(), targetCurrency));
    }

    public abstract Money doConvert(BigDecimal amount, Currency targetCurrency);

}
