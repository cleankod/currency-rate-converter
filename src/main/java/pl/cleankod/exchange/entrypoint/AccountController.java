package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.provider.FindAccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final FindAccountService findAccountService;

    public AccountController(FindAccountService findAccountService) {
        this.findAccountService = findAccountService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return findAccountService.findAccountById(Account.Id.of(URLDecoder.decode(id, StandardCharsets.UTF_8)), currency)
                .fold(
                        ResponseEntity::ok,
                        authenticationFailedReason -> ResponseEntity.notFound().build()
                );
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        return findAccountService.findAccountByNumber(Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8)), currency)
                .fold(
                        ResponseEntity::ok,
                        authenticationFailedReason -> ResponseEntity.notFound().build()
                );
    }


}
