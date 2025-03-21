package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;

    @Override
    public RequestDto create(long userId, long eventId) {
        requireUserExists(userId);
        if (!requestRepository
                .findAllByRequesterIdAndEventIdAndStatusNotLike(userId, eventId, RequestState.CANCELED)
                .isEmpty()) {
            throw new NotPossibleException("Request already exists");
        }
        Event event = eventService.getById(eventId);
        if (userId == event.getInitiatorId())
            throw new NotPossibleException("User is Initiator of event");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new NotPossibleException("Event is not published");
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit())
                throw new NotPossibleException("Request limit exceeded");
        Request newRequest = new Request();
        newRequest.setRequesterId(userId);
        newRequest.setEvent(event);
        if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
            newRequest.setStatus(RequestState.PENDING);
        } else {
            newRequest.setStatus(RequestState.CONFIRMED);
        }
        return RequestMapper.mapToRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public List<RequestDto> getAllRequestByUserId(final long userId) {
        requireUserExists(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public RequestDto cancel(final long userId, final long requestId) {
        requireUserExists(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(Request.class, requestId));
        if (!request.getRequesterId().equals(userId))
            throw new NotPossibleException("Request is not by user");
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.mapToRequestDto(requestRepository.save(request));
    }

    private void requireUserExists(long userId) {
        //TODO: проверить существование пользователя через UserClient
    }
}
