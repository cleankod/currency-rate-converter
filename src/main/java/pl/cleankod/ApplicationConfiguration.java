package pl.cleankod;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.codejargon.feather.Provides;
import pl.cleankod.exchange.core.gateway.AccountRepository;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.FindAccountAndConvertCurrencyUseCase;
import pl.cleankod.exchange.entrypoint.AccountController;
import pl.cleankod.exchange.provider.AccountInMemoryRepository;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;
import uk.org.webcompere.lightweightconfig.ConfigLoader;

import javax.inject.Singleton;
import java.util.Currency;
import java.util.Properties;

public class ApplicationConfiguration {

    @Provides
    @Singleton
    Properties configuration() {
        return ConfigLoader.loadPropertiesFromResource("application.properties");
    }

    @Provides
    @Singleton
    AccountRepository accountRepository() {
        return new AccountInMemoryRepository();
    }

    @Provides
    @Singleton
    ExchangeRatesNbpClient exchangeRatesNbpClient(Properties configuration) {
        String nbpApiBaseUrl = configuration.getProperty("provider.nbp-api.base-url");
        return Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeRatesNbpClient.class, nbpApiBaseUrl);
    }

    @Provides
    @Singleton
    CurrencyConversionService currencyConversionService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient);
    }

    @Provides
    @Singleton
    FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase(
            AccountRepository accountRepository,
            CurrencyConversionService currencyConversionService,
            Properties configuration
    ) {
        Currency baseCurrency = Currency.getInstance(configuration.getProperty("app.base-currency"));
        return new FindAccountAndConvertCurrencyUseCase(accountRepository, currencyConversionService, baseCurrency);
    }

    @Provides
    @Singleton
    AccountController accountController(ObjectMapper objectMapper, FindAccountAndConvertCurrencyUseCase findAccountAndConvertCurrencyUseCase) {
        return new AccountController(objectMapper, findAccountAndConvertCurrencyUseCase);
    }

    @Provides
    @Singleton
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
