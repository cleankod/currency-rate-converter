package pl.cleankod.exchange.adapter.entrypoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.adapter.entrypoint.mapper.DtoMapperV1;
import pl.cleankod.exchange.adapter.entrypoint.model.AccountDto;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.service.AccountService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    private final DtoMapperV1 mapper;

    public AccountController(AccountService accountService, DtoMapperV1 mapper) {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountDto> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        return accountService.get(Account.Id.of(id), Optional.ofNullable(currency))
                .map(mapper::mapToAccountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping(path = "/number={number}")
    public ResponseEntity<AccountDto> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        String decodedAccountNumber = URLDecoder.decode(number, StandardCharsets.UTF_8);
        return accountService.get(Account.Number.of(decodedAccountNumber), Optional.ofNullable(currency))
                .map(mapper::mapToAccountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
