package pl.cleankod.exchange.provider.nbp.model;

import java.util.List;

public record RateWrapper(String table, String currency, String code, List<Rate> rates) {
}
