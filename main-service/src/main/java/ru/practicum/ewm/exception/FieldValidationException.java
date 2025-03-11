package ru.practicum.ewm.exception;

import lombok.Getter;

@Getter
public class FieldValidationException extends RuntimeException {

    private final String field;
    private final String error;
    private final Object value;

    public FieldValidationException(final String field, final String error, final Object value) {
        super("Validation error: Field: %s Error: %s Value: %s".formatted(field, error, value));
        this.field = field;
        this.error = error;
        this.value = value;
    }
}
