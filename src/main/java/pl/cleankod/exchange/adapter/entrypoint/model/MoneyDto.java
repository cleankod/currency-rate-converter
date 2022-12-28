package pl.cleankod.exchange.adapter.entrypoint.model;

import java.math.BigDecimal;
import java.util.Currency;

public record MoneyDto(BigDecimal amount, Currency currency) {
}
