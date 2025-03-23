package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.request.model.RequestState;
import ru.practicum.ewm.user.client.UserClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserClient userClient;

    @Override
    public Request create(long userId, long eventId) {
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
        return requestRepository.save(newRequest);
    }

    @Override
    public List<Request> getAllRequestByUserId(final long userId) {
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    @Transactional
    public Request cancel(final long userId, final long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(Request.class, requestId));
        if (!request.getRequesterId().equals(userId))
            throw new NotPossibleException("Request is not by user");
        request.setStatus(RequestState.CANCELED);
        return requestRepository.save(request);
    }
}
