package pl.cleankod.util;

import com.google.common.base.Objects;

public class AccountJsonResponse {
    String id;
    String number;
    MoneyJsonResponse balance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public MoneyJsonResponse getBalance() {
        return balance;
    }

    public void setBalance(MoneyJsonResponse balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountJsonResponse that = (AccountJsonResponse) o;
        return Objects.equal(id, that.id) && Objects.equal(number, that.number) && Objects.equal(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, number, balance);
    }

    public class MoneyJsonResponse {
        String amount;
        String currency;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MoneyJsonResponse that = (MoneyJsonResponse) o;
            return Objects.equal(amount, that.amount) && Objects.equal(currency, that.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(amount, currency);
        }
    }
}
