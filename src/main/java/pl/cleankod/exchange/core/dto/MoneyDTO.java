package pl.cleankod.exchange.core.dto;

import pl.cleankod.exchange.core.domain.Money;

import java.math.BigDecimal;

public record MoneyDTO(BigDecimal amount, String currencyCode) {
    public static MoneyDTO from(Money money) {
        return new MoneyDTO(money.amount(), money.currency().getCurrencyCode());
    }
}
