package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;

import java.util.Currency;
import java.util.Optional;

public interface CurrencyConversionService {

    /**
     * Will convert the provided money into the target currency.
     *
     * @return empty if the conversion was not successful.
     */
    Optional<Money> convert(Money money, Currency targetCurrency);

}
