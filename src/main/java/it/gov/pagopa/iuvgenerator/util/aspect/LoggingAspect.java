package it.gov.pagopa.iuvgenerator.util.aspect;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static it.gov.pagopa.iuvgenerator.util.CommonUtility.deNull;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    public static final String START_TIME = "startTime";
    public static final String METHOD = "method";
    public static final String STATUS = "status";
    public static final String CODE = "httpCode";
    public static final String RESPONSE_TIME = "responseTime";
    public static final String FAULT_CODE = "faultCode";
    public static final String FAULT_DETAIL = "faultDetail";
    public static final String REQUEST_ID = "requestId";
    public static final String OPERATION_ID = "operationId";
    public static final String ARGS = "args";

    @Autowired
    HttpServletRequest httpRequest;

    @Autowired
    HttpServletResponse httpResponse;

    @Value("${info.application.name}")
    private String name;

    @Value("${info.application.version}")
    private String version;

    @Value("${info.properties.environment}")
    private String environment;

    private static String getDetail(ResponseEntity<ErrorResponse> result) {
        String detail;
        if (result != null && result.getBody() != null && result.getBody().getBody().getDetail() != null) {
            return result.getBody().getBody().getDetail();
        }
        return null;
    }

    private static String getTitle(ResponseEntity<ErrorResponse> result) {
        String title;
        if (result != null && result.getBody() != null && result.getBody().getBody().getTitle() != null) {
            return result.getBody().getBody().getTitle();
        }
        return null;
    }

    public static String getExecutionTime() {
        String startTime = MDC.get(START_TIME);
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - Long.parseLong(startTime);
            return String.valueOf(executionTime);
        }
        return "-";
    }

    private static Map<String, String> getParams(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        Map<String, String> params = new HashMap<>();
        int i = 0;
        for (var paramName : codeSignature.getParameterNames()) {
            params.put(paramName, deNull(joinPoint.getArgs()[i++]));
        }
        return params;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {
        // all rest controllers
    }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repository() {
        // all repository methods
    }

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void service() {
        // all service methods
    }

    /**
     * Log essential info of application during the startup.
     */
    @PostConstruct
    public void logStartup() {
        log.info("-> Starting {} version {} - environment {}", name, version, environment);
    }

    @Around(value = "restController()")
    public Object logApiInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        MDC.put(METHOD, joinPoint.getSignature().getName());
        MDC.put(START_TIME, String.valueOf(System.currentTimeMillis()));
        MDC.put(OPERATION_ID, UUID.randomUUID().toString());
        if (MDC.get(REQUEST_ID) == null) {
            var requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);
        }
        Map<String, String> params = getParams(joinPoint);
        MDC.put(ARGS, params.toString());

        log.info("Invoking API operation {} - args: {}", joinPoint.getSignature().getName(), params);

        Object result = joinPoint.proceed();

        MDC.put(STATUS, "OK");
        MDC.put(CODE, String.valueOf(httpResponse.getStatus()));
        MDC.put(RESPONSE_TIME, getExecutionTime());
        log.info("Successful API operation {} - result: {}", joinPoint.getSignature().getName(), result);
        MDC.remove(STATUS);
        MDC.remove(CODE);
        MDC.remove(RESPONSE_TIME);
        MDC.remove(START_TIME);
        return result;
    }

    @AfterReturning(value = "execution(* *..exception.ErrorHandler.*(..))", returning = "result")
    public void trowingApiInvocation(JoinPoint joinPoint, ResponseEntity<ErrorResponse> result) {
        MDC.put(STATUS, "KO");
        MDC.put(CODE, String.valueOf(result.getStatusCode().value()));
        MDC.put(RESPONSE_TIME, getExecutionTime());
        MDC.put(FAULT_CODE, getTitle(result));
        MDC.put(FAULT_DETAIL, getDetail(result));
        log.info("Failed API operation {} - error: {}", MDC.get(METHOD), result);
        MDC.clear();
    }

    @Around(value = "repository() || service()")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String, String> params = getParams(joinPoint);
        log.debug("Call method {} - args: {}", joinPoint.getSignature().toShortString(), params);
        Object result = joinPoint.proceed();
        log.debug("Return method {} - result: {}", joinPoint.getSignature().toShortString(), result);
        return result;
    }
}
