package pl.cleankod.exchange.core.exception;

import java.math.BigDecimal;

public class ExchangeRateNotApplicableException extends IllegalStateException {
    public ExchangeRateNotApplicableException(BigDecimal value) {
        super(String.format(
                        "Cannot convert currency from %s to %s because the exchange rate does not have a permissible value: "
                                + " " + value
                )
        );
    }
}
