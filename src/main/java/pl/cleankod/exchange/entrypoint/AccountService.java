package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.util.Preconditions;

import java.util.Currency;
import java.util.Optional;
import pl.cleankod.util.domain.FailureToResponseMapper;
import pl.cleankod.util.domain.ResponseEntityFactory;

public class AccountService {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                          FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public ResponseEntity<Account> findAccount(Account.Id identifier, String currency) {
        Preconditions.requireNonNull(identifier);
        return Optional.ofNullable(currency)
                .map(currencyCode -> findAccountAndConvertCurrencyUseCase.execute(identifier, Currency.getInstance(currencyCode)))
                .orElseGet(() -> findAccountUseCase.execute(identifier))
                .map(ResponseEntityFactory::ok)
                .onFailure(FailureToResponseMapper::mapFailureWithLogging).unwrap();
    }

    public ResponseEntity<Account> findAccount(Account.Number number, String currency) {
        Preconditions.requireNonNull(number);
        return Optional.ofNullable(currency)
                .map(currencyCode -> findAccountAndConvertCurrencyUseCase.execute(number, Currency.getInstance(currencyCode)))
                .orElseGet(() -> findAccountUseCase.execute(number))
                .map(ResponseEntityFactory::ok)
                .onFailure(FailureToResponseMapper::mapFailureWithLogging).unwrap();
    }
}