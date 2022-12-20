package pl.cleankod.exchange.adapter.persistence.repository.entity;

// Suggestion: use Entities for proper persistence and business layer separation
public record AccountEntity(Long id, String number, String amount, String currenncy){}


