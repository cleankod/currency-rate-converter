package pl.cleankod.exchange.entrypoint;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Optional;

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
    public HttpResponse<Account> findAccountById(@PathVariable String id, @QueryValue Optional<String> currency) {
        return currency
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), Currency.getInstance(s))
                                .map(HttpResponse::ok)
                                .orElse(HttpResponse.notFound())
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(Account.Id.of(id))
                                .map(HttpResponse::ok)
                                .orElse(HttpResponse.notFound())
                );
    }

    @Get("/number={+number}")
    public HttpResponse<Account> findAccountByNumber(@PathVariable String number, @QueryValue Optional<String> currency) {
        Account.Number accountNumber = Account.Number.of(URLDecoder.decode(number, StandardCharsets.UTF_8));
        return currency
                .map(s ->
                        findAccountAndConvertCurrencyUseCase.execute(accountNumber, Currency.getInstance(s))
                                .map(HttpResponse::ok)
                                .orElse(HttpResponse.notFound())
                )
                .orElseGet(() ->
                        findAccountUseCase.execute(accountNumber)
                                .map(HttpResponse::ok)
                                .orElse(HttpResponse.notFound())
                );
    }
}
