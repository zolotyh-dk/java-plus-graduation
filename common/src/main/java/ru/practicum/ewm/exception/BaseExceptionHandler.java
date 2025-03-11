package ru.practicum.ewm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseExceptionHandler extends HttpRequestResponseLogger {

    protected final Clock clock;

    protected BaseExceptionHandler(final Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleMissingQueryParameter(
            final MissingServletRequestParameterException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = List.of(new FieldErrorData(exception.getParameterName(),
                "no value provided", null));
        return handleFieldErrorDataInternally(ParameterType.PARAMETER, errors, httpRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleWrongTypeOfQueryParameter(
            final MethodArgumentTypeMismatchException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = List.of(new FieldErrorData(exception.getName(), "value of wrong type",
                exception.getValue()));
        return handleFieldErrorDataInternally(ParameterType.PARAMETER, errors, httpRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleParameterValidationException(
            final ParameterValidationException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = List.of(new FieldErrorData(exception.getParameter(), exception.getError(),
                exception.getValue()));
        return handleFieldErrorDataInternally(ParameterType.PARAMETER, errors, httpRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleWrongValuesInRequestBody(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest httpRequest) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = exception.getFieldErrors().stream()
                .map(error -> new FieldErrorData(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .toList();
        return handleFieldErrorDataInternally(ParameterType.FIELD, errors, httpRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(final Exception exception, final HttpServletRequest httpRequest) {
        log.error(exception.getMessage(), exception);
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Unexpected error")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }

    protected ResponseEntity<Object> handleFieldErrorDataInternally(
            final ParameterType parameterType,
            final List<FieldErrorData> errors,
            final HttpServletRequest httpRequest) {
        final List<FieldErrorData> orderedErrors = errors.stream()
                .sorted(Comparator.comparing(FieldErrorData::field).thenComparing(FieldErrorData::error))
                .toList();
        final Set<String> fields = orderedErrors.stream()
                .map(FieldErrorData::field)
                .map("'%s'"::formatted)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Wrong request format")
                .message(makeMessage(parameterType, fields, errors))
                .errors(orderedErrors)
                .timestamp(LocalDateTime.now(clock))
                .build();
        logHttpResponse(httpRequest, apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }

    protected String makeMessage(final ParameterType parameterType, final Set<String> fields,
            final List<FieldErrorData> errors) {
        final String type = parameterType == ParameterType.FIELD ? "field" : "parameter";
        if (fields.size() == 1 && errors.size() == 1) {
            return "There is an error in %s %s".formatted(type, fields.iterator().next());
        } else if (fields.size() == 1) {
            return "There are errors in %s %s".formatted(type, fields.iterator().next());
        } else {
            return "There are errors in %ss %s".formatted(type, String.join(", ", fields));
        }
    }

    protected record FieldErrorData(String field, String error, Object value) {

    }

    protected enum ParameterType {
        PARAMETER,
        FIELD
    }
}
