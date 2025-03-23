package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.client.UserClient;

import java.util.List;

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
}
