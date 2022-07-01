package pl.cleankod.exchange.core.domain;

import java.util.Currency;

public class MoneyOperationFailedReason {
    private final String message;

    public MoneyOperationFailedReason(String message) {
        this.message = message;
    }

    public static MoneyOperationFailedReason conversionFailed(Currency sourceCurrency, Currency targetCurrency) {
        var message = String.format("Cannot convert currency from %s to %s.", sourceCurrency, targetCurrency);
        return new MoneyOperationFailedReason(message);
    }

    public String getMessage() {
        return message;
    }

}
