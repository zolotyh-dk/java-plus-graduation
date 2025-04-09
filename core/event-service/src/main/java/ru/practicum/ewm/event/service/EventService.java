package ru.practicum.ewm.event.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.model.EventPatch;
import ru.practicum.ewm.event.model.Event;

import java.util.Collection;
import java.util.List;

public interface EventService {

    Event add(@NotNull @Valid Event event);

    Event getById(long id);

    Event getPublishedById(long id, long userId);

    Event getByIdAndUserId(long id, long userId);

    List<Event> get(@NotNull @Valid EventFilter filter);

    Event update(long id, @NotNull @Valid EventPatch patch);

    Event update(long id, @NotNull @Valid EventPatch patch, long userId);

    List<Event> getAvailableUpcomingEventsByIds(Collection<Long> ids);

    boolean existsById(long eventId);
}
