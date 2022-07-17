package pl.cleankod.exchange.entrypoint;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.gateway.AccountService;
import pl.cleankod.exchange.entrypoint.model.AccountResponse;
import pl.cleankod.util.Preconditions;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
//TODO introduce session for CID
//TODO do we need to introduce support for Async req?
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ApiOperation
            (
                    value = "Returns an account based on an id",
                    httpMethod = "GET"
            )
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "",
                    response = AccountResponse.class),
            @ApiResponse(code = 404,
                    message = "The requested account was not found.",
                    response = ResponseEntity.class),
            @ApiResponse(code = 400,
                    message = "Bad request",
                    response = ResponseEntity.class)
    }
    )
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountResponse findAccountById(@RequestAttribute("CID") String cid,
                                           @ApiParam(example = "fa07c538-8ce4-11ec-9ad5-4f5a625cd744", required = true) @PathVariable String id,
                                           @ApiParam(example = "EUR") @RequestParam(required = false) String currency) {
        Preconditions.validateUUIDFormat(id);
        Preconditions.validateCurrency(currency);
        return accountService.findAccountBy(Account.Id.of(UUID.fromString(id)), currency, cid);
    }

    @ApiOperation
            (
                    value = "Returns an account based on an account number",
                    httpMethod = "GET"
            )
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "",
                    response = AccountResponse.class),
            @ApiResponse(code = 404,
                    message = "The requested account was not found.",
                    response = ResponseEntity.class),
            @ApiResponse(code = 400,
                    message = "Bad request",
                    response = ResponseEntity.class)
    }
    )
    @GetMapping(path = "/number={number}")
    @ResponseStatus(HttpStatus.OK)
    public AccountResponse findAccountByNumber(@RequestAttribute("CID") String cid,
                                               @ApiParam(example = "65+1090+1665+0000+0001+0373+7343", required = true) @PathVariable String number,
                                               @ApiParam(example = "PLN") @RequestParam(required = false) String currency) {

        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        Preconditions.validateAccountNumber(accountNumber.value());
        Preconditions.validateCurrency(currency);
        return accountService.findAccountBy(accountNumber, currency, cid);
    }

}
