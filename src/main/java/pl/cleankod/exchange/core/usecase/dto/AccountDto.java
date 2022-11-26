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

        if (!getId().equals(that.getId())) return false;
        if (!getNumber().equals(that.getNumber())) return false;
        return getBalance().equals(that.getBalance());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getNumber().hashCode();
        result = 31 * result + getBalance().hashCode();
        return result;
    }

    public static final class MoneyDto {
        private BigDecimal amount;
        private String currency;

        public MoneyDto(BigDecimal amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        public MoneyDto() {
        }

        public static MoneyDto from(Money money) {
            return new MoneyDto(money.amount(), money.currency().getCurrencyCode());
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MoneyDto moneyDto)) return false;

            if (!getAmount().equals(moneyDto.getAmount())) return false;
            return getCurrency().equals(moneyDto.getCurrency());
        }

        @Override
        public int hashCode() {
            int result = getAmount().hashCode();
            result = 31 * result + getCurrency().hashCode();
            return result;
        }
    }
}
