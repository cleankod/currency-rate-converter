package pl.cleankod.exchange.entrypoint;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.util.Currency;
import java.util.Optional;

public class AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;
    public AccountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Optional<Account> find(Account.Id id, Currency currency) {
        if (currency == null) {
            return findAccountUseCase.execute(id);
        }
        return findAccountAndConvertCurrencyUseCase.execute(id, currency);
    }

    public Optional<Account> find(Account.Number number, Currency currency) {
        if (currency == null) {
            return findAccountUseCase.execute(number);
        }
        return findAccountAndConvertCurrencyUseCase.execute(number, currency);
    }
}
