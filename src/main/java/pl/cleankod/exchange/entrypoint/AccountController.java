package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;

    public AccountController(AccountRepository accountRepository, CurrencyConversionService currencyConversionService) {
        this.accountRepository = accountRepository;
        this.currencyConversionService = currencyConversionService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return accountRepository.find(Account.Id.of(id))
                .map(account -> new Account(account.id(), account.number(), convert(currency, account.balance())))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        return accountRepository.find(Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8)))
                .map(account -> new Account(account.id(), account.number(), convert(currency, account.balance())))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Money convert(String currency, Money money) {
        if (currency != null) {
            return money.convert(currencyConversionService, Currency.getInstance(currency));
        }
        return money;
    }

}
