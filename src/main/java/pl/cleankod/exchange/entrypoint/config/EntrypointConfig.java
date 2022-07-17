package pl.cleankod.exchange.entrypoint.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import pl.cleankod.exchange.entrypoint.logging.CorrelationIdInterceptor;

@SpringBootConfiguration
public class EntrypointConfig {

    @Bean
    public CorrelationIdInterceptor correlationIdInterceptor() {
        return new CorrelationIdInterceptor();
    }
}
