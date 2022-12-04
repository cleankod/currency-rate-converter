package pl.cleankod.exchange.core.usecase.dto;

import pl.cleankod.exchange.core.domain.Money;

import java.io.Serializable;
import java.math.BigDecimal;

public record MoneyDto(BigDecimal amount, String currencyCode) implements Serializable {
    public static MoneyDto from(Money money) {
        return new MoneyDto(money.amount(), money.currency().getCurrencyCode());
    }
}
