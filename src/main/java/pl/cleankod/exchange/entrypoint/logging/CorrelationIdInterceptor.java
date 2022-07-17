package pl.cleankod.exchange.entrypoint.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class CorrelationIdInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CorrelationIdInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        String requestCorrelationId = this.getOrCreateCorrelationId(request);
        MDC.put("CID", requestCorrelationId);
        request.setAttribute("CID", requestCorrelationId);

        log.info("[preHandle][" + request.getMethod() + "][ correlationId: "+ requestCorrelationId +"]" + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) {
        //TODO does not work when using ResponseEntity. More to be implemented to be able to return response with cid in headers
        response.addHeader("CID", MDC.get("CID"));
        log.info("[postHandle][ correlationId: " + MDC.get("CID") + "]" + " response with status: " + response.getStatus());
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
           log.debug(ex.getMessage());
        }
        log.info("[afterCompletion][" + request + "][ correlationId: " + MDC.get("CID") + "][exception: " + ex + "]");
        response.addHeader("CID", MDC.get("CID"));
        MDC.remove("CID");
    }

    private String getOrCreateCorrelationId(HttpServletRequest request) {
        String requestCorrelationId = request.getHeader("CID");
        if (requestCorrelationId == null) {
            requestCorrelationId = UUID.randomUUID().toString();
            log.info("No correlationId found in Header. Generated : " + requestCorrelationId);
        } else {
            log.info("Found correlationId in Header : " + requestCorrelationId);
        }
        return requestCorrelationId;
    }
}
