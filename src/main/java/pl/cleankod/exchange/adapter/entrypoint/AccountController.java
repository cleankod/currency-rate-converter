package pl.cleankod.exchange.adapter.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.service.AccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        return accountService.getById(id, Optional.ofNullable(currency))
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        String decodedAccountNumber = URLDecoder.decode(number, StandardCharsets.UTF_8);
        return accountService.getByNumber(decodedAccountNumber, Optional.ofNullable(currency))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
