package pl.cleankod.exchange.core.domain;

import java.util.UUID;

public record AccountDto(UUID id, String number, Money balance) {
}
