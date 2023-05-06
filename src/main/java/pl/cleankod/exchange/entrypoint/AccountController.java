package pl.cleankod.exchange.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import spark.Request;
import spark.Response;
import spark.Spark;

import static pl.cleankod.exchange.entrypoint.ControllerUtil.asString;
import static pl.cleankod.exchange.entrypoint.ControllerUtil.badRequest;
import static pl.cleankod.exchange.entrypoint.ControllerUtil.ok;
import static pl.cleankod.exchange.entrypoint.ControllerUtil.processError;
import static pl.cleankod.exchange.entrypoint.ControllerUtil.strToCurrency;
import static spark.Spark.exception;
import static spark.Spark.get;


public class AccountController {

    private static final String NUMBER_PREFIX = "number=";

    public AccountController(ObjectMapper objectMapper, FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase) {
        Spark.internalServerError((request, response) -> {
            response.type("application/json");
            return "{\"message\":\"Internal server error\"}";
        });
        exception(IllegalArgumentException.class, (exception, request, response) -> {
            response.type("application/json");
            ApiError apiError = badRequest(response, exception.getMessage());
            response.body(asString(objectMapper, apiError));
        });
        get("/accounts/:idOrNumber", (Request request, Response response) -> {
            String idOrNumber = request.params("idOrNumber");
            String currency = request.queryParams("currency");
            // TODO: different solution ?
            return idOrNumber.startsWith(NUMBER_PREFIX) ?
                    getByNumber(findAccountAndConvertCurrencyUseCase, idOrNumber.replace(NUMBER_PREFIX, ""), currency, response) :
                    getById(findAccountAndConvertCurrencyUseCase, idOrNumber, currency, response);
        }, objectMapper::writeValueAsString);
    }

    private Record getById(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                           String id, String currency, Response response) {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), strToCurrency(currency))
                .fold(
                        error -> processError(response, error),
                        account -> ok(response, account)
                );
    }

    private Record getByNumber(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                               String number, String currency, Response response) {
        return findAccountAndConvertCurrencyUseCase.execute(Account.Number.of(number), strToCurrency(currency))
                .fold(
                        error -> processError(response, error),
                        account -> ok(response, account)
                );
    }


}
