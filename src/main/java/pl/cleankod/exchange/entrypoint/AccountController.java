package pl.cleankod.exchange.entrypoint;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pl.cleankod.exchange.core.AccountService;
import pl.cleankod.exchange.core.domain.AccountRetrievalFailedReason;

import java.util.UUID;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Path("/{id}")
    @GET
    public Response findAccountById(@PathParam("id") UUID id, @QueryParam("currency") String currency) {
        return accountService.findAccountById(id, currency).fold(
                account -> Response.ok(account).build(),
                this::handleFailed);
    }

    @Path("/number={number}")
    @GET
    public Response findAccountByNumber(@PathParam("number") String number, @QueryParam("currency") String currency) {
        return accountService.findAccountByNumber(number, currency).fold(
                account -> Response.ok(account).build(),
                this::handleFailed);
    }

    private Response handleFailed(AccountRetrievalFailedReason failedReason) {
        if (AccountRetrievalFailedReason.ACCOUNT_NOT_FOUND.equals(failedReason)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.serverError().build();
    }


}
