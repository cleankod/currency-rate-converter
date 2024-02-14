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

    public Optional<Account> findAccountByIdAndCurrency(Account.Id id, Currency targetCurrency) {
        return targetCurrency != null ?
                findAccountAndConvertCurrencyUseCase.execute(id, targetCurrency) :
                findAccountUseCase.execute(id);
    }

    public Optional<Account> findAccountByNumberAndCurrency(Account.Number number, Currency targetCurrency) {
        return targetCurrency != null ?
                findAccountAndConvertCurrencyUseCase.execute(number, targetCurrency) :
                findAccountUseCase.execute(number);
    }
}
