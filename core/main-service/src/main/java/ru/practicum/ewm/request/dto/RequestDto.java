package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.practicum.ewm.request.model.RequestState;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record RequestDto(

        LocalDateTime created,

        long event,

        long id,

        long requester,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        RequestState status) {
}