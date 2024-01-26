package pl.cleankod.util;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

public class FeignCorrelationIdInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		String correlationId = MDC.get("correlationId");
		if (correlationId != null) {
			template.header("X-Correlation-ID", correlationId);
		}
	}
}
