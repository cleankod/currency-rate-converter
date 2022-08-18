package pl.cleankod

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.entrypoint.model.ApiError
import pl.cleankod.util.deserialize.AccountDeserializer
import spock.lang.Specification

abstract class BaseApplicationSpecification extends Specification {
  private static init = true
  private static final baseUrl = "http://localhost:8080"
  private static final ObjectMapper objectMapper = new ObjectMapper()

  static {
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Account.class, new AccountDeserializer());
    objectMapper.registerModule(module);
  }

  @SuppressWarnings('unused')
  def setupSpec() {
    if (init) {
      ApplicationInitializer.main(new String[0])
      init = false
    }
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

  static ApiError transformError(HttpResponse httpResponse) {
    return transform(httpResponse, ApiError)
  }

  private static HttpResponse execute(HttpUriRequest request) {
    return HttpClientBuilder.create().build().execute(request)
  }
}
