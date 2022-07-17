package pl.cleankod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import pl.cleankod.exchange.core.config.ExchangeConfig;
import pl.cleankod.exchange.entrypoint.config.EntrypointConfig;
import pl.cleankod.exchange.entrypoint.config.WebMvcConfig;
import pl.cleankod.exchange.entrypoint.logging.CorrelationIdInterceptor;
import pl.cleankod.exchange.provider.config.ProviderConfig;


@EnableAutoConfiguration
@Import({ExchangeConfig.class, ProviderConfig.class, WebMvcConfig.class, EntrypointConfig.class})
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }
}
