package pl.cleankod.exchange.entrypoint.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.cleankod.exchange.entrypoint.logging.CorrelationIdInterceptor;

@SpringBootConfiguration
public class WebMvcConfig implements WebMvcConfigurer {

    CorrelationIdInterceptor correlationIdInterceptor;

    public WebMvcConfig(CorrelationIdInterceptor correlationIdInterceptor) {
        this.correlationIdInterceptor = correlationIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(correlationIdInterceptor);
    }

}
