package com.gm.virtualization.api.errorhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Generic {@link com.ingenico.virtualization.api.VirtualizationServiceApi} exception handling
 */
@ControllerAdvice
public class VirtualServiceExceptionAdvice {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<VirtualizationServiceError> anyException(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isEmpty()) {
            if (ex.getStackTrace().length > 0) {
                StackTraceElement ste = ex.getStackTrace()[0];
                message = format("Exception %s found in %s (method %s) at line number %s", ex.getClass().getName(), ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
            }
        }
        VirtualizationServiceError error = new VirtualizationServiceError(message);
        return new ResponseEntity<>(error, BAD_REQUEST);
    }
}
