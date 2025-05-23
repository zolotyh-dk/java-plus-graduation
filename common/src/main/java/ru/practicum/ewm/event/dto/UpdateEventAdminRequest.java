package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.ewm.validation.NotBlankOrNull;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(

        @NotBlankOrNull
        @Size(min = 3, max = 120)
        String title,

        Long category,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
        AdminAction stateAction
) {
        public enum AdminAction {
                PUBLISH_EVENT,
                REJECT_EVENT
        }
}
