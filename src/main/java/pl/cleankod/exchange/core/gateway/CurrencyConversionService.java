package pl.cleankod.exchange.core.gateway;

import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.usecase.CurrencyConversionException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;

public interface CurrencyConversionService {

    //TODO: consider using it as config
    MathContext MC = new MathContext(2, RoundingMode.HALF_DOWN);

    default MathContext getMathContext() {
        return MC;
    }

    default Money convert(Money money, Currency targetCurrency) {
        if (money.currency().equals(targetCurrency)) {
            return money;
        }
        if (money.amount().compareTo(BigDecimal.ZERO) == 0) {
            return new Money(BigDecimal.ZERO, targetCurrency);
        }
        BigDecimal rate = getRate(money.currency(), targetCurrency);
        BigDecimal convertedAmount = money.amount().multiply(rate, getMathContext());
        return new Money(convertedAmount, targetCurrency);
    }

    BigDecimal getRate(Currency currency, Currency targetCurrency);
}
