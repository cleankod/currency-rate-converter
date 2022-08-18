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

    public AccountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        var accountId = Account.Id.of(id);

        return findAccountAndConvertCurrencyUseCase.execute(accountId, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        var accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));

        return findAccountAndConvertCurrencyUseCase.execute(accountNumber, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
