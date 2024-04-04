package it.gov.pagopa.iuvgenerator.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum AppErrorCodeMessageEnum {
    // --- Internal logic errors ---
    GENERIC_ERROR(1000, "Generic error", "Error while generating IUV. {0}", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final Integer code;
    private final String title;
    private final String detail;
    private final HttpStatusCode status;

    AppErrorCodeMessageEnum(Integer code, String title, String detail, HttpStatus status) {
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.status = status;
    }
}
