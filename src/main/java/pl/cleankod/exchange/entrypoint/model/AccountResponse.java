package pl.cleankod.exchange.entrypoint.model;

import java.math.BigDecimal;
import java.util.Currency;


public record AccountResponse(String id, String number, MoneyResponse balance) {
    public record MoneyResponse(BigDecimal amount, Currency currency) {
    }
}