package pl.cleankod.exchange.provider.nbp.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public record RateWrapper(String table, String currencyCode, String code, List<Rate> rates) {

    public MidRate getMidRate() {
        return new MidRate(rates().get(0).mid(), Currency.getInstance(code));
    }

    public record MidRate(BigDecimal rate, Currency currency) {
    }
}
