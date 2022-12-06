package pl.cleankod.exchange.core.usecase;

import pl.cleankod.exchange.core.domain.Account;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class FindAccountDelegator {
    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public FindAccountDelegator(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Optional<Account> findById(String id, Optional<String> currency) {
        return currency
                .map(c -> findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(c)))
                .orElseGet(() -> findAccountUseCase.execute(Account.Id.of(id)));
    }

    public Optional<Account> findByNumber(String number, Optional<String> currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return currency
                .map(c -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(c)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber));
    }
}
