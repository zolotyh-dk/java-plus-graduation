package ru.practicum.ewm.event;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.practicum.ewm.request.RequestState;

@Component
class EventRequestDtoValidatorExtension implements Validator {

    @Override
    public boolean supports(@NonNull final Class<?> clazz) {
        return UpdateEventRequestStatusDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull final Object target, @NonNull final Errors errors) {
        UpdateEventRequestStatusDto request = (UpdateEventRequestStatusDto) target;
        if (request.status() != RequestState.CONFIRMED && request.status() != RequestState.REJECTED) {
            errors.rejectValue("status", "status.not.allowed",
                    "new status must be %s or %s".formatted(RequestState.CONFIRMED, RequestState.REJECTED));
        }
    }
}
