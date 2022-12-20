package pl.cleankod.exchange.adapter.provider.nbp.dto;

import java.math.BigDecimal;

public record RateDto(String no, String effectiveDate, BigDecimal mid) {
}
