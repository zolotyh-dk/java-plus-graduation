package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EventRequestStatusDto;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.dto.UpdateEventRequestStatusDto;
import ru.practicum.ewm.event.client.EventClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.user.client.UserClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestEnrichmentService {
    private final RequestService requestService;
    private final RequestMapper requestMapper;
    private final UserClient userClient;
    private final EventClient eventClient;

    public RequestDto create(long userId, long eventId) {
        checkUserExists(userId);
        EventFullDto event = getEvent(eventId);
        return requestMapper.mapToRequestDto(requestService.create(userId, event));
    }

    public List<RequestDto> getAllRequestByUserId(final long userId) {
        checkUserExists(userId);
        List<Request> requests = requestService.getAllRequestByUserId(userId);
        return requestMapper.mapToRequestDto(requests);
    }

    public RequestDto cancel(final long userId, final long requestId) {
        checkUserExists(userId);
        return requestMapper.mapToRequestDto(requestService.cancel(userId, requestId));
    }

    public List<RequestDto> getRequests(long userId, long eventId) {
        checkEventExists(userId, eventId);
        return requestMapper.mapToRequestDto(requestService.getRequests(userId, eventId));
    }

    public EventRequestStatusDto processRequests(long eventId, UpdateEventRequestStatusDto updateDto, long userId) {
        EventFullDto event = getEvent(eventId);
        Pair<List<Request>, List<Request>> processedRequests = requestService.processRequests(event, updateDto, userId);
        return new EventRequestStatusDto(requestMapper.mapToRequestDto(processedRequests.getFirst()),
                requestMapper.mapToRequestDto(processedRequests.getSecond()));
    }

    private void checkUserExists(long userId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }

    private void checkEventExists(long userId, long eventId) {
        if (!eventClient.existsById(userId, eventId)) {
            throw new NotFoundException("Event", eventId);
        }
    }

    private EventFullDto getEvent(long eventId) {
        return eventClient.getById(eventId);
    }
}
