package pl.cleankod.util;

import org.slf4j.MDC;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter implements Filter {

	private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER_NAME);
		if (correlationId == null) {
			correlationId = UUID.randomUUID().toString();
		}

		MDC.put("correlationId", correlationId);

		try {
			chain.doFilter(request, response);
		} finally {
			MDC.remove("correlationId");
		}
	}
}
