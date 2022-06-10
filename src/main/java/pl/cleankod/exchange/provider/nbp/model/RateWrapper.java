package pl.cleankod.exchange.provider.nbp.model;

import pl.cleankod.util.Preconditions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public record RateWrapper(String table, String currency, String code, List<Rate> rates) {

    public RateWrapper {
        Preconditions.requireNonNull(code);
        Preconditions.requireNonNull(table);
        Preconditions.requireNonNull(currency);
        Preconditions.requireNonNull(rates);
        if (rates.isEmpty()) {
            throw new ExternalClientParameterException("Rates not found!");
        }
    }

    public MidRate getMidRate() {
        return new MidRate(rates().get(0).mid(), Currency.getInstance(code));
    }

    public record MidRate(BigDecimal rate, Currency currency) {

        public MidRate {
            Preconditions.requireNonNull(rate);
            Preconditions.requireNonNull(currency);
            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                throw new ExternalClientParameterException("Mid rate can not be less then 0!");
            }
        }

    }
}
