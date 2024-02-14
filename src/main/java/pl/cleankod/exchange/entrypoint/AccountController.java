package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        final Currency targetCurrency = Optional.ofNullable(currency).map(Currency::getInstance).orElse(null);
        return accountService.findAccountByIdAndCurrency(Account.Id.of(id), targetCurrency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        final Currency targetCurrency = Optional.ofNullable(currency).map(Currency::getInstance).orElse(null);
        return accountService.findAccountByNumberAndCurrency(accountNumber, targetCurrency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
