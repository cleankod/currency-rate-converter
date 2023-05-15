package pl.cleankod.util.domain;

import java.util.Currency;
import java.util.Optional;

public final class Currencies {

    private Currencies() {
    }

    public static Currency getOrNull(String currency) {
        return Optional.ofNullable(currency)
                .map(s -> Currency.getInstance(currency))
                .orElse(null);
    }
}
