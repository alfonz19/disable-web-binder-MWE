package com.example.is_web_binder_used_to_bind_jackson_dto_request_body;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
@AllArgsConstructor
public final class ResponseEntityExceptionHandler
        extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

    @ExceptionHandler(value = AuthenticationServiceException.class)
    @ResponseBody
    public ResponseEntity<Object> handleAuthenticationServiceException(RuntimeException ex, WebRequest request) {
        return handle(HttpStatus.UNAUTHORIZED, ex, request);
    }


    /*
     * Bean validation related handler.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        RestError body = createRestError(HttpStatus.BAD_REQUEST,
                constraintViolationsToErrorMessage(ex.getConstraintViolations()),
                getPath(request));
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private static String constraintViolationsToErrorMessage(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream()
                .map(ResponseEntityExceptionHandler::constraintViolationToMessage)
                .collect(Collectors.joining(", "));
    }

    private static String constraintViolationToMessage(ConstraintViolation<?> unwrappedViolation) {
        boolean violationHasPropertyPath = unwrappedViolation.getPropertyPath().toString().isEmpty();
        return violationHasPropertyPath
               ? unwrappedViolation.getMessage()
               : String.format("path '%s': %s",
                       unwrappedViolation.getPropertyPath(),
                       unwrappedViolation.getMessage());
    }


    //----------

    /*
     * This is called when validation of incoming rest request fails.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {
        String path = getPath(request);
        String violationsMessage = describeBindingResultErrors(ex.getBindingResult())
                .collect(Collectors.joining(", "));

        RestError body = createRestError(statusCode, violationsMessage, path);
        return handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    //----------

    /*
     * This is called when message of incoming rest request is somewhat garbled or cannot be read for some reason.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode statusCode,
                                                                  @NonNull WebRequest request) {
        return handle(statusCode, ex, request);
    }

    //----------

    private static Stream<String> describeBindingResultErrors(BindingResult bindingResult) {
        List<ObjectError> fieldErrors = bindingResult.getAllErrors();
        try {
            return fieldErrors.stream()
                    .map(fieldError -> constraintViolationToMessage(fieldError.unwrap(ConstraintViolation.class)));
        } catch (IllegalArgumentException e) {
            //if transformation fails, for any reason, but namely say unwrapping, we would like to provide
            //different, but consistent error messages.
            return fieldErrors.stream()
                    .map(ObjectError::toString);
        }
    }

    private RestError createRestError(HttpStatusCode httpStatusCode, String message, String path) {
        return new RestError(OffsetDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
                httpStatusCode.value(),
                message,
                path);
    }

    private <T extends RuntimeException> ResponseEntity<Object> handle(HttpStatusCode statusCode, T ex, WebRequest request) {
        return handle(statusCode, ex, ex.getMessage(), request);
    }

    private <T extends RuntimeException> ResponseEntity<Object> handle(HttpStatusCode statusCode, T ex, String message, WebRequest request) {
        RestError restError = createRestError(statusCode, message, getPath(request));
        return handleExceptionInternal(ex, restError, new HttpHeaders(), statusCode, request);
    }

    private static String getPath(WebRequest request) {
        if ( request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        } else {
            return request.getDescription(false);
        }
    }

    @Setter
    @Getter
    public static class RestError {
        private final String timestamp;
        private final int httpStatusCode;
        private final String httpStatus;
        private final String message;
        private final String path;

        @java.beans.ConstructorProperties({"timestamp", "httpStatusCode", "message", "path"})
        public RestError(String timestamp, int httpStatusCode, String message, String path) {
            this.timestamp = timestamp;
            this.httpStatusCode = httpStatusCode;
            this.httpStatus = getReasonPhrase(httpStatusCode);
            this.message = message;
            this.path = path;
        }

        private static String getReasonPhrase(int httpStatusCode) {
            try {
                return HttpStatus.valueOf(httpStatusCode).getReasonPhrase();
            } catch (IllegalArgumentException e) {
                return "non-standard status code";
            }
        }
    }
}
