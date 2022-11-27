package pl.cleankod

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.entrypoint.exception.ErrorResponse
import spock.lang.Specification

import java.time.LocalDate

abstract class BaseApplicationSpecification extends Specification {
  static startDate = LocalDate.now().minusDays(5)
  static endDate = LocalDate.now().minusDays(1)

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

  static ErrorResponse transformError(HttpResponse httpResponse) {
    return transform(httpResponse, ErrorResponse)
  }

  static String getNbpResponseWithRate(String exchangeRate) {
    return String.format("{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"026/A/NBP/2022\",\"effectiveDate\":\"2022-02-08\",\"mid\":%s}]}", exchangeRate)
  }

  private static HttpResponse execute(HttpUriRequest request) {
    return HttpClientBuilder.create().build().execute(request)
  }
}
