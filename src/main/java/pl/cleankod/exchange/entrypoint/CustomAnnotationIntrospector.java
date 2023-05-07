package pl.cleankod.exchange.entrypoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import pl.cleankod.exchange.core.domain.SingleValueObject;

import java.lang.reflect.Method;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.DEFAULT;
import static pl.cleankod.exchange.core.domain.SingleValueObject.VALUE_METHOD_NAME;

class CustomAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        return isSingleValueObject(a.getRawType()) ? DEFAULT : super.findCreatorAnnotation(config, a);
    }

    @Override
    public Boolean hasAsValue(Annotated annotated) {
        if (annotated instanceof AnnotatedMethod method && isValueMethodOfSingleValueObject(method.getMember())) {
            return true;
        }

        return super.hasAsValue(annotated);
    }

    private static boolean isValueMethodOfSingleValueObject(Method method) {
        return isSingleValueObject(method.getDeclaringClass())
                && method.getName().equals(VALUE_METHOD_NAME)
                && method.getParameterCount() == 0;
    }

    private static boolean isSingleValueObject(Class clazz) {
        return SingleValueObject.class.isAssignableFrom(clazz);
    }
}
