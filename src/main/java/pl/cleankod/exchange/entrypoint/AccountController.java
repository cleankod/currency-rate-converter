package pl.cleankod.exchange.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.exceptions.CurrencyConversionException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        final Currency targetCurrency = Optional.ofNullable(currency).map(this::getCurrency).orElse(null);
        return accountService.findAccountByIdAndCurrency(Account.Id.of(id), targetCurrency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        final Currency targetCurrency = Optional.ofNullable(currency).map(this::getCurrency).orElse(null);
        return accountService.findAccountByNumberAndCurrency(accountNumber, targetCurrency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Currency getCurrency(final String currencyId) {
        try {
            return Currency.getInstance(currencyId);
        } catch (Exception exception) {
            LOGGER.error(String.format("Exception while retrieving currency for id %s.", currencyId), exception);
            throw new CurrencyConversionException(String.format("Currency with id %s does not exist.", currencyId));
        }
    }

}
