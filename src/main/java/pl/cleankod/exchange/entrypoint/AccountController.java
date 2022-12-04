package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.dto.AccountDTO;
import pl.cleankod.exchange.core.usecase.AccountService;

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
    public ResponseEntity<AccountDTO> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return accountService.find(Account.Id.of(id), currency)
                .map(AccountDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<AccountDTO> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return accountService.find(accountNumber, currency)
                .map(AccountDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
