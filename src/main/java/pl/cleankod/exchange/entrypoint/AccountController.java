package pl.cleankod.exchange.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cleankod.exchange.core.domain.Account;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.entrypoint.model.ApiError;
import pl.cleankod.util.domain.AppErrors;
import spark.Request;
import spark.Response;
import spark.Spark;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static pl.cleankod.exchange.entrypoint.ControllerUtil.*;
import static spark.Spark.exception;
import static spark.Spark.get;


public class AccountController {

    //TODO: add logs to other classes, add metrics and traceability
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

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
        logFindById(id, currency);
        return findAccountAndConvertCurrencyUseCase.execute(Account.Id.of(id), strToCurrency(currency))
                .fold(
                        error -> {
                            logFindByIdError(id, currency, error);
                            return processError(response, error);
                        },
                        account -> {
                            logFindByIdSuccess(id, currency, account);
                            return ok(response, account);
                        }
                );
    }

    private Record getByNumber(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                               String number, String currency, Response response) {
        logFindByNumber(number, currency);
        return findAccountAndConvertCurrencyUseCase.execute(Account.Number.of(number), strToCurrency(currency))
                .fold(
                        error -> {
                            logFindByNumberError(number, currency, error);
                            return processError(response, error);
                        },
                        account -> {
                            logFindByNumberSuccess(number, currency, account);
                            return ok(response, account);
                        }
                );
    }

    private void logFindById(String id, String currency) {
        log.debug("Find account by id {} {}",
                keyValue("id", id),
                keyValue("currency", currency)
        );
    }

    private void logFindByIdError(String id, String currency, AppErrors.Base error) {
        log.error("Error finding account by id {} {} {}",
                keyValue("id", id),
                keyValue("currency", currency),
                keyValue("error", error)
        );
    }

    private void logFindByIdSuccess(String id, String currency, Account account) {
        log.debug("Found account by id {} {} {}",
                keyValue("id", id),
                keyValue("currency", currency),
                keyValue("account", account)
        );
    }

    private void logFindByNumber(String number, String currency) {
        log.debug("Find account by number {} {}",
                keyValue("number", number),
                keyValue("currency", currency)
        );
    }

    private void logFindByNumberError(String number, String currency, AppErrors.Base error) {
        log.error("Error finding account by number {} {} {}",
                keyValue("number", number),
                keyValue("currency", currency),
                keyValue("error", error)
        );
    }

    private void logFindByNumberSuccess(String number, String currency, Account account) {
        log.debug("Found account by number {} {} {}",
                keyValue("number", number),
                keyValue("currency", currency),
                keyValue("account", account)
        );
    }


}
