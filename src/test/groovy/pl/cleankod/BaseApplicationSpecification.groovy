package pl.cleankod

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.env.Environment
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.entrypoint.model.ApiError
import spock.lang.Shared
import spock.lang.Specification

@MicronautTest(application = ApplicationInitializer.class)
abstract class BaseApplicationSpecification extends Specification {
  private static final ObjectMapper objectMapper = new ObjectMapper()

  @Shared
  @Inject
  private EmbeddedServer embeddedServer
  @Shared
  private String baseUrl

  @SuppressWarnings('unused')
  def setupSpec() {
    baseUrl = "http://${embeddedServer.host}:${embeddedServer.port}"
    println(baseUrl)
  }

  def <T> T get(String path, Class<T> responseType) {
    HttpResponse response = getResponse(path)
    return transform(response, responseType)
  }

  HttpResponse getResponse(String path) {
    HttpGet httpGet = new HttpGet(baseUrl + path)
    return execute(httpGet)
  }

  def <T> T transform(HttpResponse httpResponse, Class<T> classOfT) {
    def response = EntityUtils.toString(httpResponse.getEntity())
    return objectMapper.readValue(response, classOfT)
  }

  ApiError transformError(HttpResponse httpResponse) {
    return transform(httpResponse, ApiError)
  }

  static HttpResponse execute(HttpUriRequest request) {
    return HttpClientBuilder.create().build().execute(request)
  }
}
