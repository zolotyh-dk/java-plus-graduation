package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record EventFullDto(
        Long id,
        UserShortDto initiator,
        String title,
        CategoryDto category,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        Location location,
        String annotation,
        String description,
        Long participantLimit,
        Boolean paid,
        Boolean requestModeration,
        Long confirmedRequests,
        double rating,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        EventState state) {

}
