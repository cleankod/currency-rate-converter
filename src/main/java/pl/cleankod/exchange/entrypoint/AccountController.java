package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return Optional.ofNullable(currency)
                .map(Currency::getInstance)
                .map(_currency -> findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), _currency))
                .orElseGet(() -> findAccountUseCase.execute(Account.Id.of(id)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(Currency::getInstance)
                .map(_currency -> findAccountAndConvertCurrencyUseCase.execute(accountNumber, _currency))
                .orElseGet(() -> findAccountUseCase.execute(accountNumber))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
