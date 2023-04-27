package pl.cleankod.exchange.entrypoint.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.mapper.AccountMapper;
import pl.cleankod.exchange.entrypoint.model.AccountWrapper;
import pl.cleankod.exchange.entrypoint.service.AccountServiceImpl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final AccountServiceImpl accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountServiceImpl accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping(path = "/{id}")
    public AccountWrapper findAccountById(@PathVariable String id, @RequestParam(required = false) String currency) {
        log.info("Request received to find account with id: {}", id);
        return accountMapper.mapToAccountWrapper(accountService.findAccountById(Account.Id.of(id), currency));

    }

    @GetMapping(path = "/number={number}")
    public AccountWrapper findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency) {
        log.info("Request received to find account with number: {}", number);
        var accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return accountMapper.mapToAccountWrapper(accountService.findAccountByNumber(accountNumber, currency));
    }


}
