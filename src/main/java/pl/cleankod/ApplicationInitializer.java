package pl.cleankod;

import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorator;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.AccountService;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.AccountServiceImpl;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.core.usecase.FindAccountUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.entrypoint.ExceptionHandlerAdvice;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.NbpRateService;
import pl.cleankod.exchange.provider.nbp.CustomNbpErrorDecoder;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import pl.cleankod.util.CorrelationIdFilter;
import pl.cleankod.util.FeignCorrelationIdInterceptor;

import java.util.Currency;

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableCaching
public class ApplicationInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationInitializer.class, args);
	}

	@Bean
	AccountRepository accountRepository() {
		return new AccountInMemoryRepository();
	}

	@Bean
	public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
		FilterRegistrationBean<CorrelationIdFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CorrelationIdFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

	@Bean
	ExchangeRatesNbpClient exchangeRatesNbpClient(Environment environment, CircuitBreakerRegistry circuitBreakerRegistry) {
		String nbpApiBaseUrl = environment.getRequiredProperty("provider.nbp-api.base-url");

		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("nbpCircuitBreaker");
		FeignDecorator decorators = FeignDecorators.builder()
				.withCircuitBreaker(circuitBreaker)
				.build();

		return Resilience4jFeign.builder(decorators)
				.client(new ApacheHttpClient())
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.logger(new Slf4jLogger(ExchangeRatesNbpClient.class))
				.logLevel(feign.Logger.Level.FULL)
				.errorDecoder(new CustomNbpErrorDecoder())
				.requestInterceptor(new FeignCorrelationIdInterceptor())
				.target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
	}

	@Bean
	NbpRateService nbpRateService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
		return new NbpRateService(exchangeRatesNbpClient);
	}

	@Bean
	CurrencyConversionService currencyConversionService(NbpRateService nbpRateService) {
		return new CurrencyConversionNbpService(nbpRateService);
	}

	@Bean
	FindAccountUseCase findAccountUseCase(AccountRepository accountRepository) {
		return new FindAccountUseCase(accountRepository);
	}

	@Bean
	FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
			AccountRepository accountRepository,
			CurrencyConversionService currencyConversionService,
			Environment environment
	) {
		Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
		return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
	}

	@Bean
	AccountService accountService(FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase,
	                              FindAccountUseCase findAccountUseCase) {
		return new AccountServiceImpl(findAccountAndConvertCurrencyUseCase, findAccountUseCase);
	}

	@Bean
	AccountController accountController(AccountService accountService) {
		return new AccountController(accountService);
	}

	@Bean
	ExceptionHandlerAdvice exceptionHandlerAdvice() {
		return new ExceptionHandlerAdvice();
	}


}
