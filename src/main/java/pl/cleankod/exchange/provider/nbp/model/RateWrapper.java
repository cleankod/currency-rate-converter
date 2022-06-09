package pl.cleankod.exchange.provider.nbp.model;

import java.math.BigDecimal;
import java.util.List;

public record RateWrapper(String table, String currency, String code, List<Rate> rates) {

    public MidRate getMidRate() {
        return new MidRate(rates().get(0).mid());
    }

    public record MidRate(BigDecimal rate) {
    }
}
