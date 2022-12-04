package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.util.Preconditions;

import java.util.Currency;
import java.util.Optional;

public class AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Optional<Account> find(Account.Id accountId, String currency) {
        Preconditions.requireNonNull(accountId);
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(accountId, Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(accountId));
    }

    public Optional<Account> find(Account.Number accountNumber, String currency) {
        Preconditions.requireNonNull(accountNumber);
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber));
    }
}
