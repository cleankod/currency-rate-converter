package pl.cleankod;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import pl.cleankod.exchange.core.gateway.*;
import pl.cleankod.exchange.core.mapper.AccountMapperImpl;
import pl.cleankod.exchange.core.service.CircuitBreakerImpl;
import pl.cleankod.exchange.core.service.FinderServiceImpl;
import pl.cleankod.exchange.core.service.ServiceConvertorImpl;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.CurrencyConversionStubService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;

@SpringBootConfiguration
@EnableAutoConfiguration
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }

    @Bean
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Bean
    ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment) {
        String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Bean
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient);
    }

    @Bean
    FindAccountUseCase findAccountUseCase(AccountRepository accountRepository, AccountMapper accountMapper) {
        return new FindAccountUseCase(accountRepository, accountMapper);
    }

    @Bean
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository, AccountMapper accountMapper
    ) {
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, accountMapper);
    }

    @Bean
    AccountController accountController(FinderService finderService, FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
                                        FindAccountUseCase findAccountUseCase) {
        return new AccountController(finderService, findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    ExceptionHandlerAdvice exceptionHandlerAdvice() {
        return new ExceptionHandlerAdvice();
    }

    @Bean
    AccountMapper accountMapper(ServiceConvertor serviceConvertor) {
        return new AccountMapperImpl(serviceConvertor);
    }

    @Bean
    ServiceConvertor serviceConvertor(Environment environment, CircuitBreaker circuitBreaker) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new ServiceConvertorImpl(circuitBreaker, baseCurrency);
    }

    @Bean
    CircuitBreaker circuitBreaker(CurrencyConversionService currencyConversionService) {
        return new CircuitBreakerImpl(currencyConversionService);
    }

    @Bean
    CurrencyConversionService currencyConversionStubService() {
        return new CurrencyConversionStubService();
    }

    @Bean
    FinderService finderService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase, FindAccountUseCase findAccountUseCase) {
        return new FinderServiceImpl(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
    }

    @Bean
    public CommonsRequestLoggingFilter loggingFilter(Environment environment) {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setEnvironment(environment);
        loggingFilter.setMaxPayloadLength(64000);
        return loggingFilter;
    }
}
