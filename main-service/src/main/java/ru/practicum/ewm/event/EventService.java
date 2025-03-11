package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.request.RequestDto;

import java.util.List;

public interface EventService {

    Event add(@NotNull @Valid Event event);

    Event getById(long id);

    Event getPublishedById(long id);

    Event getByIdAndUserId(long id, long userId);

    List<Event> get(@NotNull @Valid EventFilter filter);

    Event update(long id, @NotNull @Valid EventPatch patch);

    Event update(long id, @NotNull @Valid EventPatch patch, long userId);

    List<RequestDto> getRequests(long userId, long eventId);

    EventRequestStatusDto processRequests(long id, @NotNull @Valid UpdateEventRequestStatusDto dto, long userId);
}
