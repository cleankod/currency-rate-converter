package pl.cleankod.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.cleankod.exchange.core.gateway.CurrencyConversionService;
import pl.cleankod.exchange.core.usecase.ConvertAccountCurrencyService;
import pl.cleankod.exchange.core.usecase.ConvertAccountCurrencyServiceImpl;
import pl.cleankod.exchange.provider.CurrencyConversionNbpService;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Currency;

@Configuration
public class ConvertCurrencyConfiguration {
    @Bean
    CurrencyConversionService currencyConversionNbpService(ExchangeRatesNbpClient exchangeRatesNbpClient) {
        return new CurrencyConversionNbpService(exchangeRatesNbpClient);
    }

    @Bean
    ConvertAccountCurrencyService convertAccountCurrencyService(
            @Autowired CurrencyConversionService currencyConversionService,
            @Autowired Environment environment) {
        Currency baseCurrency = Currency.getInstance(environment.getRequiredProperty("app.base-currency"));
        return new ConvertAccountCurrencyServiceImpl(currencyConversionService, baseCurrency);
    }
}
