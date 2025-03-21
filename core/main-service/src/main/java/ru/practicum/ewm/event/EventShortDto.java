package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.practicum.ewm.category.CategoryDto;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record EventShortDto(

        Long id,
        Long initiator,
        String title,
        CategoryDto category,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        String annotation,
        Boolean paid,
        Long confirmedRequests,
        Long views
) {
}
