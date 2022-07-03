package pl.cleankod.trace;

import io.micrometer.core.lang.NonNull;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class ContextInitializeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {

        MDC.put("TraceId", UUID.randomUUID().toString());
        return true;
    }
}
