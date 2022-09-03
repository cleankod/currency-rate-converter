package pl.cleankod.aspect;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ControllerAspect {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);
    private final ObjectMapper mapper;

    public ControllerAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GetMapping mapping = signature.getMethod().getAnnotation(GetMapping.class);

        Map<String, Object> parameters = getParameters(joinPoint);

        try {
            logger.info("Entry controller method: path: {}, method type: Get, arguments: {} ",
                    mapping.path(), mapper.writeValueAsString(parameters));
        } catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    public void logMethodAfterReturn(JoinPoint joinPoint, ResponseEntity<?> entity) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GetMapping mapping = signature.getMethod().getAnnotation(GetMapping.class);

        try {
            logger.info("Exit controller method: path: {}, response: {}",
                    mapping.path(), mapper.writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "exception")
    public void logMethodAfterThrow(JoinPoint joinPoint, Exception exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GetMapping mapping = signature.getMethod().getAnnotation(GetMapping.class);

        try {
            logger.info("Exit controller method: path: {}, response: {}",
                    mapping.path(), mapper.writeValueAsString(exception.getMessage()));
        } catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();

        HashMap<String, Object> map = new HashMap<>();

        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], joinPoint.getArgs()[i]);
        }

        return map;
    }
}
