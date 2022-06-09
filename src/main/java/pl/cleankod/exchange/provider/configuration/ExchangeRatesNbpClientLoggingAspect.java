package pl.cleankod.exchange.provider.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
public class ExchangeRatesNbpClientLoggingAspect {

    private final Logger log = Logger.getLogger(ExchangeRatesNbpClient.class.getName());

    @Pointcut("execution(* pl.cleankod.exchange.provider.nbp.ExchangeRatesNbpClient.*(..))")
    void externalApiCallMethod() {
    }

    @Before("externalApiCallMethod()")
    void logBeforePerformingExternalApiCall(JoinPoint joinPoint) {
        log.info("Is about to call NBP with parameters: " + Arrays.toString(joinPoint.getArgs()));
    }

}
