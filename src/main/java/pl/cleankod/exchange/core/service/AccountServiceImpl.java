package pl.cleankod.exchange.core.service;

import io.smallrye.common.constraint.Nullable;
import pl.cleankod.exchange.core.AccountService;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.exception.UnknownCurrencyException;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;
import pl.cleankod.util.Result;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

public class AccountServiceImpl implements AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountServiceImpl(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }
    public Result<Account, AccountRetrievalFailedReason> findAccountById(UUID id, @Nullable String currency) {
        return Optional.ofNullable(currency)
                .map(currentCurrency -> findAccountAndConvertCurrencyUseCase.execute
                        (id, getCurrency(currentCurrency)))
                .orElseGet(() -> findAccountUseCase.execute(id));
    }

    public Result<Account, AccountRetrievalFailedReason> findAccountByNumber(String number, @Nullable String currency) {
        String accountNumber = URLDecoder.decode(number, StandardCharsets.UTF_8);
        return Optional.ofNullable(currency)
                .map(currentCurrency -> findAccountAndConvertCurrencyUseCase
                        .execute(accountNumber, getCurrency(currentCurrency)))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber));
    }

    private Currency getCurrency(String currentCurrency) {
        try {
            return Currency.getInstance(currentCurrency);
        } catch (IllegalArgumentException exception) {
            throw new UnknownCurrencyException(currentCurrency);
        }
    }

}
