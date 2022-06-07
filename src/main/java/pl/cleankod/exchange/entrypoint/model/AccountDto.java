package pl.cleankod.exchange.entrypoint.model;

import java.util.UUID;

/**
 * These value objects are pulled from let's say database. In the future we might want change the inner implementation,
 * for instance we want to change Money into class Money(BigDecimal decimalValue, BigDecimal integerValue, Currency currency...)
 * If we would keep Account and Money we would need to make some hacky way to serialize it. If we change that to Dto we would need
 * to make a simple adding 2 big decimals as string.
 */
public record AccountDto(UUID id, String number, MoneyDto balance) {}
