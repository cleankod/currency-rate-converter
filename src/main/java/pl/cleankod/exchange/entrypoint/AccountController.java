package pl.cleankod.exchange.entrypoint;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import org.slf4j.MDC;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Controller("/accounts")
public class AccountController {

    private final FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase;
    private final FindAccountUseCase findAccountUseCase;

    public AccountController(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                             FindAccountUseCase findAccountUseCase) {
        this.findAccountAndConvertCurrencyUseCase = findAccountAndConvertCurrencyUseCase;
        this.findAccountUseCase = findAccountUseCase;
    }

    @Get("/{id}")
    public HttpResponse<Account> findAccountById(@PathVariable String id, @Nullable @QueryValue String currency) {
        MDC.put("requestId", UUID.randomUUID().toString());
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(s))
                                .map(HttpResponseFactory.INSTANCE::ok)
                                .orElseGet(() -> HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_FOUND))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(Account.Id.of(id))
                                .map(HttpResponseFactory.INSTANCE::ok)
                                .orElseGet(() -> HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_FOUND))
                );
    }

    @Get("/number={number}")
    public HttpResponse<Account> findAccountByNumber(@PathVariable String number, @Nullable @QueryValue String currency) {
        MDC.put("requestId", UUID.randomUUID().toString());
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return Optional.ofNullable(currency)
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s))
                                .map(HttpResponseFactory.INSTANCE::ok)
                                .orElseGet(() -> HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_FOUND))
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                                .map(HttpResponseFactory.INSTANCE::ok)
                                .orElseGet(() -> HttpResponseFactory.INSTANCE.status(HttpStatus.NOT_FOUND))
                );
    }
}
