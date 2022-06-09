package pl.cleankod.exchange.entrypoint.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.logging.Logger;

@Aspect
public class ControllersLoggingAspect {

    private final Logger log = Logger.getLogger(ControllersLoggingAspect.class.getName());

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    Object logControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Controller has been executed: " + joinPoint.toShortString());
        Object result = joinPoint.proceed();
        log.info("Successfully finished api call. Result: " + result.toString());
        return result;
    }

    @AfterThrowing(
            pointcut = "@annotation(org.springframework.web.bind.annotation.GetMapping)",
            throwing = "error"
    )
    void logExceptionFromController(JoinPoint jp, Throwable error) {
        log.warning("Error from " + jp.toShortString() + " has been thrown! Error: " + error.getMessage());
    }
}
