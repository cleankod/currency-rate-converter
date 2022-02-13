package pl.cleankod

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import spock.lang.Specification

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

  static <T> T get(String path, Class<T> responseType) {
    HttpGet httpGet = new HttpGet(baseUrl + path)
    HttpResponse response = execute(httpGet)
    return transform(response, responseType)
  }

  static <T> T transform(HttpResponse httpResponse, Class<T> classOfT) {
    def response = EntityUtils.toString(httpResponse.getEntity())
    return objectMapper.readValue(response, classOfT)
  }

  private static HttpResponse execute(HttpUriRequest request) {
    return HttpClientBuilder.create().build().execute(request)
  }
}
