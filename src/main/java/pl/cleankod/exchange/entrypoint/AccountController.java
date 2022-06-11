package pl.cleankod.exchange.entrypoint;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import pl.cleankod.exchange.core.aggregator.AccountAggregator;
import pl.cleankod.exchange.core.dto.AccountDto;
import pl.cleankod.exchange.exception.error.ErrorMessage;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Api(tags = "Account APIs")
public class AccountController {

    private final AccountAggregator accountAggregator;

    public AccountController(final AccountAggregator accountAggregator) {
        this.accountAggregator = accountAggregator;
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Retrieve account based on id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account retrieved successfully"),
            @ApiResponse(code = 404, message = "Account not found", response = ErrorMessage.class)
    })
    public AccountDto findAccountById(
            @ApiParam(value = "Account id", required = true) @PathVariable final UUID id,
            @ApiParam(value = "Currency") @RequestParam(required = false) final String currency) {
        return accountAggregator.findAccountById(id, currency);
    }

    @GetMapping(path = "/number={number}")
    @ApiOperation(value = "Retrieve account based on number")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account retrieved successfully"),
            @ApiResponse(code = 404, message = "Account not found", response = ErrorMessage.class)
    })
    public AccountDto findAccountByNumber(
            @ApiParam(value = "Account number", required = true) @PathVariable final String number,
            @ApiParam(value = "Currency") @RequestParam(required = false) final String currency) {
        return accountAggregator.findAccountByNumber(number, currency);
    }

}
