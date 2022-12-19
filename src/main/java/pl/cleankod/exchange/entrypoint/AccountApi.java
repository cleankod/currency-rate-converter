package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.cleankod.exchange.core.domain.Account;

@Tag(name = "Accounts", description = "Account Controller")
public interface AccountApi {

    @GetMapping(path = "/{id}")
    @Operation(summary = "findAccountById", description = "Returns Account details and amount converted to specified currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Details"),
            @ApiResponse(responseCode = "404", description = "No accounts were found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    ResponseEntity<Account> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency);

    @GetMapping(path = "/number={number}")
    @Operation(summary = "findAccountByNumber", description = "Returns Account details and amount converted to specified currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account Details"),
            @ApiResponse(responseCode = "404", description = "No accounts were found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    ResponseEntity<Account> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency);
}
