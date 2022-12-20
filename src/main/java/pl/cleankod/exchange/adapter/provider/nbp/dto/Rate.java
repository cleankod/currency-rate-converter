package pl.cleankod.exchange.adapter.provider.nbp.dto;

import java.math.BigDecimal;

//TODO: refactor to DTO taking into account serialization
public record Rate(String no, String effectiveDate, BigDecimal mid) {
}
