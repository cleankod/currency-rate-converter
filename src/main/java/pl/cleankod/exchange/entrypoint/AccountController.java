package pl.cleankod.exchange.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/accounts")
public class AccountController implements AccountControllerAPI{

    private final AccountService accountService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id,
                                                   @RequestParam(required = false) String currency) {
        LOGGER.info("Logging Request received for accountId {} & currency {}", id, currency);
        Account.Id accountId = Account.Id.of(id);
        return accountService.findAccount(accountId, currency);
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number,
                                                       @RequestParam(required = false) String currency) {
        LOGGER.info("Logging Request received for accountNumber {} & currency {}", number, currency);
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return accountService.findAccount(accountNumber, currency);
    }

}