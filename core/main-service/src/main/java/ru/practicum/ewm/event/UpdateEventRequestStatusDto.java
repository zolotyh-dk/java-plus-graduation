package ru.practicum.ewm.event;

import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.request.RequestState;

import java.util.List;

public record UpdateEventRequestStatusDto(

        List<Long> requestIds,

        @NotNull
        RequestState status
) {

}
