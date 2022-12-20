package pl.cleankod.exchange.adapter.provider.nbp.dto;

import java.util.List;

public record RateWrapperDto(String table, String currency, String code, List<Rate> rates) {
}
