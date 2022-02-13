package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.gateway.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    private final CurrencyConversionService currencyConverter = new CurrencyConversionService() {
        private static final BigDecimal PLN_TO_EUR_RATE = BigDecimal.valueOf(4.58d);
        private static final BigDecimal EUR_TO_PLN_RATE = BigDecimal.valueOf(0.22d);
        @Override
        public Money convert(Money money, Currency targetCurrency) {
            return money.currency().equals(targetCurrency)
                    ? money
                    : calculate(money, targetCurrency);
        }

        private Money calculate(Money money, Currency targetCurrency) {
            BigDecimal rate = "PLN".equals(targetCurrency.getCurrencyCode()) ? EUR_TO_PLN_RATE : PLN_TO_EUR_RATE;
            return Money.of(money.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP), targetCurrency);
        }
    };

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
            return money.convert(currencyConverter, Currency.getInstance(currency));
        }
        return money;
    }

}
