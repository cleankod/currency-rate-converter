package pl.cleankod.exchange.core.dto;

import pl.cleankod.exchange.core.domain.Money;

import java.util.UUID;

public record AccountDTO(UUID id, String number, Money balance) {
}
