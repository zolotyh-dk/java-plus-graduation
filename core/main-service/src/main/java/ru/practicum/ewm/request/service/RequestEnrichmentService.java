package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.user.client.UserClient;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestEnrichmentService {
    private final RequestService requestService;
    private final RequestMapper requestMapper;
    private final UserClient userClient;

    public RequestDto create(long userId, long eventId) {
        checkUserExists(userId);
        return requestMapper.mapToRequestDto(requestService.create(userId, eventId));
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

    private void checkUserExists(long userId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }

    private UserShortDto findUser(Event event) {
        return findUsers(Collections.singletonList(event)).getFirst();
    }

    private List<UserShortDto> findUsers(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        return userClient.findAllByIdIn(userIds);
    }
}
