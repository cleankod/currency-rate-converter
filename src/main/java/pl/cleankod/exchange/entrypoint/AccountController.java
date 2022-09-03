package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.service.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return ResponseEntity.ok(accountService.findAccountByIdAndCurrency(id, currency));
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        return ResponseEntity.ok(accountService.findAccountByNumberAndCurrency(number, currency));

    }
}
