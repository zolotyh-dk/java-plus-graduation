package ru.practicum.ewm.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
class EventDtoValidatorExtension implements Validator {

    private final Clock clock;
    private final Duration adminTimeout;
    private final Duration userTimeout;

    EventDtoValidatorExtension(
            final Clock clock,
            @Value("${ewm.timeout.admin}") final Duration adminTimeout,
            @Value("${ewm.timeout.user}") final Duration userTimeout
    ) {
        this.clock = clock;
        this.adminTimeout = adminTimeout;
        this.userTimeout = userTimeout;
    }

    @Override
    public boolean supports(@NonNull final Class<?> clazz) {
        return NewEventDto.class.isAssignableFrom(clazz)
                || UpdateEventAdminRequest.class.isAssignableFrom(clazz)
                || UpdateEventUserRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull final Object target, @NonNull final Errors errors) {
        switch (target) {
            case NewEventDto dto -> checkTimeout(dto.eventDate(), userTimeout, errors);
            case UpdateEventAdminRequest dto -> checkTimeout(dto.eventDate(), adminTimeout, errors);
            case UpdateEventUserRequest dto -> checkTimeout(dto.eventDate(), userTimeout, errors);
            default -> {
            }
        }
    }

    private void checkTimeout(final LocalDateTime value, final Duration timeout, final Errors errors) {
        final LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        if (value != null && !Duration.between(now, value.minus(timeout)).isPositive()) {
            errors.rejectValue("eventDate", "too.early",
                    "must be not earlier than in %s from now".formatted(timeout));
        }
    }
}
