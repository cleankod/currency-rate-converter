package pl.cleankod.exchange.entrypoint;

import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;
import pl.cleankod.exchange.entrypoint.model.AccountNotFoundException;
import pl.cleankod.exchange.entrypoint.model.AccountResponse;
import pl.cleankod.exchange.entrypoint.model.IncorrectFormatException;
import pl.cleankod.util.Preconditions;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public AccountResponse findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) throws AccountNotFoundException, IncorrectFormatException {
        return accountService.findAccountBy(Account.Id.of(Preconditions.validateAndReturnUUID(id)), currency);
    }

    @GetMapping(path = "/number={number}")
    public AccountResponse findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) throws AccountNotFoundException {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        Preconditions.validateAccountNumber(accountNumber.value());
        return accountService.findAccountBy(accountNumber, currency);
    }

}
