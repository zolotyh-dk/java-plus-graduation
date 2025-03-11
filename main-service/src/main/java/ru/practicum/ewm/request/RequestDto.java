package ru.practicum.ewm.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
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