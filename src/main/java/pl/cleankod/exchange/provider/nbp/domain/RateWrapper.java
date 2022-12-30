package pl.cleankod.exchange.provider.nbp.domain;

import java.util.List;

public record RateWrapper(String table, String currency, String code, List<Rate> rates) {
}
