package ru.practicum.ewm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
class ControllerExceptionHandler extends BaseExceptionHandler {

    ControllerExceptionHandler(final Clock clock) {
        super(clock);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleWrongValuesInQueryString(
            final HandlerMethodValidationException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = exception.getValueResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldErrorData(
                                result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage(),
                                result.getArgument())))
                .toList();
        return handleFieldErrorDataInternally(ParameterType.PARAMETER, errors, httpRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleFieldValidationException(
            final FieldValidationException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = List.of(new FieldErrorData(exception.getField(), exception.getError(),
                exception.getValue()));
        return handleFieldErrorDataInternally(ParameterType.FIELD, errors, httpRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleDatabaseConstraintViolation(
            final DataIntegrityViolationException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotPossibleException(
            final NotPossibleException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("For the requested operation the conditions are not met")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFoundException(
            final NotFoundException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }
}
