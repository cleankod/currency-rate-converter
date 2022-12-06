package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.domain.Money;
import pl.cleankod.exchange.core.usecase.FindAccountDelegator;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final FindAccountDelegator findAccountDelegator;

    public AccountController(FindAccountDelegator findAccountDelegator) {
        this.findAccountDelegator = findAccountDelegator;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam Optional<String> currency) {
        return findAccountDelegator.findById(id, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam Optional<String> currency) {
        return findAccountDelegator.findByNumber(number, currency)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
