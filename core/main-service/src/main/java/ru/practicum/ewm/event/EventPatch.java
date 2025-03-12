package ru.practicum.ewm.event;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.validation.NotBlankOrNull;

import java.time.LocalDateTime;

@Builder
public record EventPatch(

        @NotBlankOrNull
        @Size(min = 3, max = 120)
        String title,

        Category category,
        LocalDateTime eventDate,
        Location location,

        @NotBlankOrNull
        @Size(min = 20, max = 2000)
        String annotation,

        @NotBlankOrNull
        @Size(min = 20, max = 7000)
        String description,

        @PositiveOrZero
        Long participantLimit,

        Boolean paid,
        Boolean requestModeration,
        EventState state
) {

}
