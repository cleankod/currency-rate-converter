package pl.cleankod


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import pl.cleankod.exchange.entrypoint.model.ApiError
import pl.cleankod.util.JsonTransformer
import spock.lang.Specification

import java.util.function.Function

abstract class BaseApplicationSpecification extends Specification {
    private static init = true
    private static final baseUrl = "http://localhost:8080"
    private static final Gson GSON = new GsonBuilder().create();

    @SuppressWarnings('unused')
    def setupSpec() {
        if (init) {
            ApplicationInitializer.main(new String[0])
            init = false
        }
    }

    static <T, W> T get(String path, Function<W, T> wrapperToModel, Class<W> wrapperClass) {
        HttpResponse response = getResponse(path)
        return transform(response, wrapperToModel, wrapperClass)
    }

    static HttpResponse getResponse(String path) {
        HttpGet httpGet = new HttpGet(baseUrl + path)
        return execute(httpGet)
    }

    static <T, W> T transform(HttpResponse httpResponse, Function<W, T> wrapperToModel, Class<W> wrapperClass) {
        def response = EntityUtils.toString(httpResponse.getEntity())
        T result = wrapperToModel.apply(JsonTransformer.fromJson(response, wrapperClass))
        return result
    }


    static ApiError transformError(HttpResponse httpResponse) {
        return transform(httpResponse, { w -> new ApiError(w.message) }, ApiErrorWrapper.class)
    }

    static class ApiErrorWrapper {
        String message

        ApiErrorWrapper(String message) {
            this.message = message
        }
    }

    private static HttpResponse execute(HttpUriRequest request) {
        return HttpClientBuilder.create().build().execute(request)
    }
}
