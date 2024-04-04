package it.gov.pagopa.iuvgenerator.controller.advice;

import it.gov.pagopa.iuvgenerator.exception.AppErrorCodeMessageEnum;
import it.gov.pagopa.iuvgenerator.exception.AppException;
import it.gov.pagopa.iuvgenerator.util.CommonUtility;
import it.gov.pagopa.iuvgenerator.util.aspect.LoggingAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.time.Instant;

/**
 * All Exceptions are handled by this class
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_CODE_TITLE = "error-code.%s.title";

    private static final String ERROR_CODE_DETAIL = "error-code.%s.detail";

    private final MessageSource messageSource;

    @Value("${exception.error-code.uri}")
    private String errorCodeUri;

    @ExceptionHandler(AppException.class)
    public ErrorResponse handleAppException(AppException appEx) {
        return forAppException(appEx);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception ex) {
        String operationId = MDC.get(LoggingAspect.OPERATION_ID);
        log.error(String.format("GenericException: operation-id=[%s]", operationId != null ? operationId : "n/a"), ex);
        return forAppException(new AppException(ex, AppErrorCodeMessageEnum.GENERIC_ERROR, ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (body == null && ex instanceof ErrorResponse errorResponse) {
            ProblemDetail problemDetail = errorResponse.updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());
            setExtraProperties(problemDetail);
            body = problemDetail;
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ErrorResponse forAppException(AppException appEx) {
        return ErrorResponse.builder(appEx, forAppErrorCodeMessageEnum(appEx.getError(), appEx.getMessage()))
                .titleMessageCode(String.format(ERROR_CODE_TITLE, appEx.getError().name()))
                .detailMessageCode(String.format(ERROR_CODE_DETAIL, appEx.getError().name()))
                .detailMessageArguments(appEx.getArgs())
                .build(messageSource, LocaleContextHolder.getLocale());
    }

    private ProblemDetail forAppErrorCodeMessageEnum(AppErrorCodeMessageEnum error, @Nullable String detail) {
        Assert.notNull(error, "AppErrorCodeMessageEnum is required");

        ProblemDetail problemDetail = ProblemDetail.forStatus(error.getStatus());
        problemDetail.setType(getTypeFromErrorCode(CommonUtility.getAppCode(error)));
        problemDetail.setTitle(error.getTitle());
        problemDetail.setDetail(detail);

        problemDetail.setProperty("error-code", CommonUtility.getAppCode(error));
        setExtraProperties(problemDetail);

        return problemDetail;
    }

    private void setExtraProperties(ProblemDetail problemDetail) {
        problemDetail.setProperty("timestamp", Instant.now());
        String operationId = MDC.get(LoggingAspect.OPERATION_ID);
        if (operationId != null) {
            problemDetail.setProperty("operation-id", operationId);
        }
    }

    private URI getTypeFromErrorCode(String errorCode) {
        return new DefaultUriBuilderFactory()
                .uriString(errorCodeUri)
                .pathSegment(errorCode)
                .build();
    }

}
