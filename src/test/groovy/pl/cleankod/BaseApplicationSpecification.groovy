package pl.cleankod

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.module.SimpleModule
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.core.domain.Account
import pl.cleankod.exchange.entrypoint.model.ApiError
import pl.cleankod.util.AccountDeserializer
import pl.cleankod.util.ApiErrorDeserializer
import spock.lang.Specification

abstract class BaseApplicationSpecification extends Specification {
    private static init = true
    private static final baseUrl = "http://localhost:8080"
    private static final ObjectMapper objectMapper = new ObjectMapper()

    @SuppressWarnings('unused')
    def setupSpec() {
        if (init) {
            SimpleModule module = new SimpleModule()
            module.setDeserializerModifier(new BeanDeserializerModifier() {
                @Override
                JsonDeserializer<?> modifyDeserializer(
                        DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                    if (beanDesc.getBeanClass() == Account.class) {
                        return new AccountDeserializer()
                    } else if (beanDesc.getBeanClass() == ApiError.class) {
                        return new ApiErrorDeserializer()
                    }
                    return deserializer
                }
            })

            objectMapper.registerModule(module)
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
