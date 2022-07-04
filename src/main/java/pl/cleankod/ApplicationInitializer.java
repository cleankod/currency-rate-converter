package pl.cleankod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import pl.cleankod.exchange.config.ApplicationConfiguration;

@EnableAutoConfiguration
@Import(ApplicationConfiguration.class)
public class ApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }
}
