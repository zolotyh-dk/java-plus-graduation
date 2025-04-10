package ru.practicum.ewm.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.util.Pair;
import ru.practicum.ewm.request.dto.UpdateEventRequestStatusDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.model.Request;

import java.util.List;
import java.util.Map;

public interface RequestService {
    Request create(long userId, EventFullDto event);

    List<Request> getAllRequestByUserId(long userId);

    Request cancel(long userId, long requestId);

    List<Request> getRequests(long userId, long eventId);

    Pair<List<Request>, List<Request>> processRequests(EventFullDto event,
                                                       @NotNull @Valid UpdateEventRequestStatusDto dto,
                                                       long userId);

    Map<Long, Long> getConfirmedRequestsStats(List<Long> eventIds);

    boolean existsByRequesterIdAndEventId(long userId, long requestId);
}
