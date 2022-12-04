package pl.cleankod.exchange.provider.nbp.domain;

import java.math.BigDecimal;

public record Rate(String no, String effectiveDate, BigDecimal mid) {
}
