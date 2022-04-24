package pl.cleankod.exchange.entrypoint.number;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpRequestWrapper;
import io.micronaut.web.router.DefaultRouter;
import io.micronaut.web.router.RouteBuilder;
import io.micronaut.web.router.UriRouteMatch;
import jakarta.inject.Singleton;

import java.util.Collection;
import java.util.List;

@Singleton
@Replaces(DefaultRouter.class)
public class NumberRewriteRouter extends DefaultRouter {

    public NumberRewriteRouter(RouteBuilder... builders) {
        super(builders);
    }

    public NumberRewriteRouter(Collection<RouteBuilder> builders) {
        super(builders);
    }

    @Override
    @NonNull
    public <T, R> List<UriRouteMatch<T, R>> findAllClosest(@NonNull HttpRequest<?> request) {
        return super.findAllClosest(new HttpRequestPathRewriter<>(request));
    }

    private static class HttpRequestPathRewriter<B> extends HttpRequestWrapper<B> {

        private final String path;

        public HttpRequestPathRewriter(HttpRequest<B> delegate) {
            super(delegate);
            String originalPath = delegate.getPath();
            if (originalPath.startsWith("/accounts/number="))
                path = originalPath.replaceAll("\\+", "%20");
            else
                path = originalPath;
        }

        @Override
        @NonNull
        public String getPath() {
            return path;
        }
    }
}
