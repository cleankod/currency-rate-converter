package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;

import java.util.Currency;
import java.util.Optional;

public class ExchangeFacade {
    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public ExchangeFacade(
            FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
            FindAccountUseCase findAccountUseCase
    ) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Optional<Account> handle(String id, String currency) {
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(Account.Id.of(id)));
    }

    public Optional<Account> handle(Account.Number accountNumber, String currency) {
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber));

    }
}
