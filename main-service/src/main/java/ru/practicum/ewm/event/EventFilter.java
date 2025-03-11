package ru.practicum.ewm.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record EventFilter(

        List<Long> users,
        List<Long> categories,
        List<EventState> states,
        String text,
        Boolean paid,
        Boolean onlyAvailable,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        EventSort sort,

        @NotNull
        @PositiveOrZero
        Integer from,

        @NotNull
        @Positive
        Integer size
) {


}
