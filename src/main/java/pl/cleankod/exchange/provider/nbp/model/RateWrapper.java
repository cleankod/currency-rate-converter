package pl.cleankod.exchange.provider.nbp.model;

import pl.cleankod.util.Preconditions;

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

}
