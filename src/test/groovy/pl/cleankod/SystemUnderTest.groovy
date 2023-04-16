package pl.cleankod

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.util.SocketUtils
import pl.cleankod.exchange.entrypoint.model.ApiError

/**
 * Eases black-box testing.
 */
class SystemUnderTest {
    private final port = getFreePort()
    private final baseUrl = "http://localhost:$port"
    private ConfigurableApplicationContext applicationContext
    private static final ObjectMapper objectMapper = new ObjectMapper()

    static def getFreePort() {
        return SocketUtils.findAvailableTcpPort()
    }

    def startWithRandomPort() {
        applicationContext = ApplicationInitializer.run(new String[] {"--server.port=${port}"})
    }

    def stop() {
        if (applicationContext != null) {
            applicationContext.stop()
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

    static <T> T transform(HttpResponse httpResponse, Class<T> classOfT) {
        def response = EntityUtils.toString(httpResponse.getEntity())
        return objectMapper.readValue(response, classOfT)
    }

    static ApiError transformError(HttpResponse httpResponse) {
        return transform(httpResponse, ApiError)
    }

    private static HttpResponse execute(HttpUriRequest request) {
        return HttpClientBuilder.create().build().execute(request)
    }
}
