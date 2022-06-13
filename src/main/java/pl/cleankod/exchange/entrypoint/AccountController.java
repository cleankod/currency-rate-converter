package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.model.*;

import java.util.Currency;

@RestController
@RequestMapping("/accounts")
@Validated
public class AccountController {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    @GetMapping(path = "/{id}", params = "currency")
    ResponseEntity<AccountDto> findAccountById(@PathVariable AccountIdDto id, Currency currency) {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id.id()), currency)
                .map(this::toAccountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<AccountDto> findAccountById(@PathVariable AccountIdDto id) {
        return findAccountUseCase.execute(Account.Id.of(id.id()))
                .map(this::toAccountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}", params = "currency")
    ResponseEntity<AccountDto> findAccountByNumber(@PathVariable @AccountNumber AccountNumberDto number, Currency currency) {
        Account.Number accountNumber = Account.Number.of(number.decode());

        return findAccountAndConvertCurrencyUseCase.execute(accountNumber, currency)
                .map(this::toAccountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    ResponseEntity<AccountDto> findAccountByNumber(@PathVariable @AccountNumber AccountNumberDto number) {
        Account.Number accountNumber = Account.Number.of(number.decode());

        return findAccountUseCase.execute(accountNumber)
                .map(this::toAccountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private AccountDto toAccountDto(Account account) {
        return new AccountDto(account.id().value(), account.number().value(), new MoneyDto(account.balance().amount(), account.balance().currency()));
    }

}
