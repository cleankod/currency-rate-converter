package pl.cleankod.exchange.provider.nbp.model;

import java.math.BigDecimal;

public record Rate(String no, String effectiveDate, BigDecimal mid) {
}
