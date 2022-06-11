package pl.cleankod.exchange.config.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.aggregator.AccountAggregator;
import pl.cleankod.exchange.core.aggregator.AccountAggregatorImpl;
import pl.cleankod.exchange.core.cache.CacheService;
import pl.cleankod.exchange.core.mapper.AccountMapper;
import pl.cleankod.exchange.core.repository.AccountRepository;
import pl.cleankod.exchange.core.service.AccountService;
import pl.cleankod.exchange.core.service.AccountServiceImpl;
import pl.cleankod.exchange.core.service.CurrencyConversionNbpServiceImpl;
import pl.cleankod.exchange.core.service.CurrencyConversionService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;

@Configuration
class ServiceConfiguration {

    @Bean
    CurrencyConversionService currencyConversionService(
            final ExchangeRatesNbpClient exchangeRatesNbpClient, final ObjectMapper objectMapper,
            final CacheService cacheService, final CircuitBreaker circuitBreaker, final Environment environment) {
        final Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new CurrencyConversionNbpServiceImpl(
                exchangeRatesNbpClient, objectMapper, cacheService, circuitBreaker, baseCurrency);
    }

    @Bean
    AccountService accountService(final AccountRepository accountRepository) {
        return new AccountServiceImpl(accountRepository);
    }

    @Bean
    AccountAggregator accountAggregatorImpl(final AccountService accountService,
                                            final CurrencyConversionService currencyConversionService,
                                            final AccountMapper accountMapper) {
        return new AccountAggregatorImpl(accountService, currencyConversionService, accountMapper);
    }

}
