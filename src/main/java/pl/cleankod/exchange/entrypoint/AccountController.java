package pl.cleankod.exchange.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.usecase.FindAccountDelegator;
import pl.cleankod.exchange.entrypoint.model.AccountDTO;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final FindAccountDelegator findAccountDelegator;

    public AccountController(FindAccountDelegator findAccountDelegator) {
        this.findAccountDelegator = findAccountDelegator;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountDTO> findAccountById(@PathVariable String id, @RequestParam Optional<String> currency) {
        return findAccountDelegator.findById(id, currency)
                .map(AccountDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<AccountDTO> findAccountByNumber(@PathVariable String number, @RequestParam Optional<String> currency) {
        return findAccountDelegator.findByNumber(number, currency)
                .map(AccountDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
