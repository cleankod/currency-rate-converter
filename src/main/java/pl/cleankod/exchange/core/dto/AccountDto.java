package pl.cleankod.exchange.core.dto;

import java.util.UUID;

public record AccountDto(UUID id, String number, MoneyDto balance) {
}
