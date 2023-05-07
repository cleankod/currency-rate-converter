package pl.cleankod

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.entrypoint.CustomAnnotationIntrospector
import pl.cleankod.exchange.entrypoint.model.ApiError

class BlackBox {
    private final ObjectMapper objectMapper = new ObjectMapper()
    private String baseUrl
    private ApplicationContext context

    BlackBox() {
        objectMapper.setAnnotationIntrospector(new CustomAnnotationIntrospector())
    }

    def start() {
        if (context == null) {
            context = ApplicationInitializer.run()
            int port = context.getBean(EmbeddedServer).getPort()
            baseUrl = "http://localhost:$port"
        }
    }

    def stop() {
        if (context != null) {
            context.stop()
        }
    }

    <T> T get(String path, Class<T> responseType) {
        HttpResponse response = getResponse(path)
        return transform(response, responseType)
    }

    HttpResponse getResponse(String path) {
        HttpGet httpGet = new HttpGet(baseUrl + path)
        return execute(httpGet)
    }

    <T> T transform(HttpResponse httpResponse, Class<T> classOfT) {
        def response = EntityUtils.toString(httpResponse.getEntity())
        return objectMapper.readValue(response, classOfT)
    }

    ApiError transformError(HttpResponse httpResponse) {
        return transform(httpResponse, ApiError)
    }

    private static HttpResponse execute(HttpUriRequest request) {
        return HttpClientBuilder.create().build().execute(request)
    }
}
