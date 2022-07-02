package pl.cleankod.exchange.entrypoint.model;

import java.util.UUID;

public record AccountDto(UUID id, String number, MoneyDto balance) {
}