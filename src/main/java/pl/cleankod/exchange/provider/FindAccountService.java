package pl.cleankod.exchange.provider;

import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.util.AccountFailedReason;
import pl.cleankod.util.Preconditions;
import pl.cleankod.util.Result;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class FindAccountService {
    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public FindAccountService(
            FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
            FindAccountUseCase findAccountUseCase
    ) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public Result<Account, AccountFailedReason> findAccountById(String id, String currency) {
        Preconditions.requireNonNull(id);

        Account.Id accountId = Account.Id.of(URLDecoder.decode(id, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(accountId, Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(accountId))
                .map(Result::<Account, AccountFailedReason>successful)
                .orElseGet(() -> Result.fail(AccountFailedReason.NOT_FOUND));
    }

    public Result<Account, AccountFailedReason> findAccountByNumber(String number, String currency) {
        Preconditions.requireNonNull(number);

        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(s -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber))
                .map(Result::<Account, AccountFailedReason>successful)
                .orElseGet(() -> Result.fail(AccountFailedReason.NOT_FOUND));
    }
}
