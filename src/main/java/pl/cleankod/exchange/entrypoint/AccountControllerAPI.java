package pl.cleankod.exchange.entrypoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.entrypoint.model.ApiError;

@Tag(name="Accounts", description="Retrieves accounts info")
public interface AccountControllerAPI {

    @Operation(summary = "Find account by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping(path = "/{id}")
    ResponseEntity<?> findAccountById(@PathVariable String id, @RequestParam(required = false) String currency);

    @Operation(summary = "Find account by number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping(path = "/number={number}")
    ResponseEntity<?> findAccountByNumber(@PathVariable String number, @RequestParam(required = false) String currency);
}
