package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.service.AccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        Account.Id accountId = Account.Id.of(id);
        Currency requestedCurrency = Optional.ofNullable(currency)
                .map(s -> Currency.getInstance(currency))
                .orElse(null);

        return ResponseEntity.of(accountService.find(accountId, requestedCurrency));
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        Currency requestedCurrency = Optional.ofNullable(currency)
                .map(s -> Currency.getInstance(currency))
                .orElse(null);

        return ResponseEntity.of(accountService.find(accountNumber, requestedCurrency));
    }
}
