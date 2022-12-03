package pl.cleankod.exchange.core.usecase;

import org.springframework.http.ResponseEntity;
import pl.cleankod.exchange.core.domain.Account;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

public class AccountUseCase {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountUseCase(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    public ResponseEntity<Account> executeFindAccountById(String id, String currency) {
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(s))
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(Account.Id.of(id))
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                );
    }

    public ResponseEntity<Account> executeFindAccountByNumber(String number, String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s))
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                )
                .orElseGet(() ->
                                findAccountUseCase.execute(accountNumber)
                                        .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build())
                );
    }
}
