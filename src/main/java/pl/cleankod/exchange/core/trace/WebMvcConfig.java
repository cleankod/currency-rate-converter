package pl.cleankod.exchange.core.trace;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    ContextInitializeInterceptor contextInitializeInterceptor;

    public WebMvcConfig(ContextInitializeInterceptor contextInitializeInterceptor) {
        this.contextInitializeInterceptor = contextInitializeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextInitializeInterceptor);
    }
}
