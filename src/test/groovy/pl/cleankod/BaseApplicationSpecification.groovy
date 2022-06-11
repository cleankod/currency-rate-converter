package pl.cleankod

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.exception.error.Error
import spock.lang.Specification

import javax.annotation.PostConstruct

abstract class BaseApplicationSpecification extends Specification {
    private static init = true
    private static final baseUrl = "http://localhost:8080"
    private static final ObjectMapper objectMapper = new ObjectMapper()

    @SuppressWarnings('unused')
    def setupSpec() {
        if (init) {
            ApplicationInitializer.main(new String[0])
            init = false
        }
    }

    @PostConstruct
    def setupObjectMapper() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    static <T> T get(String path, Class<T> responseType) {
        HttpResponse response = getResponse(path)
        return transform(response, responseType)
    }

    static HttpResponse getResponse(String path) {
        HttpGet httpGet = new HttpGet(baseUrl + path)
        return execute(httpGet)
    }

    static <T> T transform(HttpResponse httpResponse, Class<T> classOfT) {
        def response = EntityUtils.toString(httpResponse.getEntity())
        return objectMapper.readValue(response, classOfT)
    }

    static Error transformError(HttpResponse httpResponse) {
        return transform(httpResponse, Error)
    }

    private static HttpResponse execute(HttpUriRequest request) {
        return HttpClientBuilder.create().build().execute(request)
    }
}
