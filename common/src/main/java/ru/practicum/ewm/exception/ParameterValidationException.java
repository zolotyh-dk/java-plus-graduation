package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class ParameterValidationException extends RuntimeException {

    private final String parameter;
    private final String error;
    private final Object value;

    public ParameterValidationException(final String parameter, final String error, final Object value) {
        super("Validation error: Parameter: %s Error: %s Value: %s".formatted(parameter, error, value));
        this.parameter = parameter;
        this.error = error;
        this.value = value;
    }
}
