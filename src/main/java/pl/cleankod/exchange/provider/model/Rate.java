package pl.cleankod.exchange.provider.model;

import java.math.BigDecimal;

public record Rate(String no, String effectiveDate, BigDecimal mid) {
}
