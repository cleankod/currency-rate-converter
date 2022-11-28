package pl.cleankod.exchange.core.usecase.dto;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;

import java.math.BigDecimal;

public final class AccountDto {

    private String id;
    private String number;
    private MoneyDto balance;

    public AccountDto(Account.Id id, Account.Number number, Money balance) {
        this.id = id.value().toString();
        this.number = number.value();
        this.balance = MoneyDto.from(balance);
    }

    public AccountDto() {
    }

    public static AccountDto from(Account account) {
        return new AccountDto(account.id(), account.number(), account.balance());
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public MoneyDto getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDto that)) return false;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getNumber() != null ? !getNumber().equals(that.getNumber()) : that.getNumber() != null) return false;
        return getBalance() != null ? getBalance().equals(that.getBalance()) : that.getBalance() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getNumber() != null ? getNumber().hashCode() : 0);
        result = 31 * result + (getBalance() != null ? getBalance().hashCode() : 0);
        return result;
    }

    public record MoneyDto(BigDecimal amount, String currencyCode) {
        public static MoneyDto from(Money money) {
            return new MoneyDto(money.amount(), money.currency().getCurrencyCode());
        }
    }
}
