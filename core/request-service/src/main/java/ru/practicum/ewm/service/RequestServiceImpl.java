package ru.practicum.ewm.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.request.dto.UpdateEventRequestStatusDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.request.dto.RequestState;
import ru.practicum.ewm.stats.message.ActionTypeProto;
import ru.practicum.ewm.stats.message.UserActionProto;
import ru.practicum.ewm.stats.service.UserActionControllerGrpc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Override
    public Request create(long userId, EventFullDto event) {
        if (!requestRepository
                .findAllByRequesterIdAndEventIdAndStatusNotLike(userId, event.id(), RequestState.CANCELED)
                .isEmpty()) {
            throw new NotPossibleException("Request already exists");
        }
        if (userId == event.initiator().id())
            throw new NotPossibleException("User is Initiator of event");
        if (!event.state().equals(EventState.PUBLISHED))
            throw new NotPossibleException("Event is not published");
        if (event.participantLimit() != 0 && event.confirmedRequests() >= event.participantLimit())
            throw new NotPossibleException("Request limit exceeded");
        Request newRequest = new Request();
        newRequest.setRequesterId(userId);
        newRequest.setEventId(event.id());
        if (event.requestModeration() && event.participantLimit() != 0) {
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

    @Override
    public List<Request> getRequests(final long userId, final long eventId) {
        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    @Transactional
    public Pair<List<Request>, List<Request>> processRequests(final EventFullDto event,
                                                              final UpdateEventRequestStatusDto dto,
                                                              final long userId) {
        if (CollectionUtils.isEmpty(dto.requestIds())) {
            return Pair.of(List.of(), List.of());
        }
        final List<Request> requests = requestRepository
                .findAllByEventIdAndIdIn(event.id(), dto.requestIds());
        requireAllExist(dto.requestIds(), requests);
        requireAllHavePendingStatus(requests);

        List<Request> confirmedRequests = List.of();
        List<Request> rejectedRequests = List.of();
        if (dto.status() == RequestState.REJECTED) {
            rejectedRequests = setStatusAndSaveAll(requests, RequestState.REJECTED);
        } else {
            final long availableSlots = event.participantLimit() == 0
                    ? Long.MAX_VALUE
                    : event.participantLimit() - event.confirmedRequests();
            if (requests.size() > availableSlots) {
                throw new NotPossibleException("Not enough available participation slots");
            }
            confirmedRequests = setStatusAndSaveAll(requests, RequestState.CONFIRMED);
            if (requests.size() == availableSlots) {
                final List<Request> pendingRequests = requestRepository
                        .findAllByEventIdAndStatus(event.id(), RequestState.PENDING);
                rejectedRequests = setStatusAndSaveAll(pendingRequests, RequestState.REJECTED);
            }
        }
        return Pair.of(confirmedRequests, rejectedRequests);
    }

    @Override
    public Map<Long, Long> getConfirmedRequestsStats(List<Long> eventIds) {
        List<EventRequestStats> confirmedRequestStats = requestRepository.getConfirmedRequestStats(eventIds);
        return confirmedRequestStats.stream()
                .collect(Collectors.toMap(EventRequestStats::getId, EventRequestStats::getRequests));
    }

    private void requireAllExist(final List<Long> ids, final List<Request> requests) {
        final Set<Long> idsFound = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toSet());
        final Set<Long> idsMissing = ids.stream()
                .filter(id -> !idsFound.contains(id))
                .collect(Collectors.toSet());
        if (!idsMissing.isEmpty()) {
            throw new NotFoundException(Request.class, idsMissing);
        }
    }

    private void requireAllHavePendingStatus(final List<Request> requests) {
        final Set<Long> idsNotPending = requests.stream()
                .filter(request -> request.getStatus() != RequestState.PENDING)
                .map(Request::getId)
                .collect(Collectors.toSet());
        if (!idsNotPending.isEmpty()) {
            final String idsStr = idsNotPending.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new NotPossibleException("Request(s) %s with wrong status (must be %s)"
                    .formatted(idsStr, RequestState.PENDING));
        }
    }

    private List<Request> setStatusAndSaveAll(final List<Request> requests, final RequestState status) {
        if (CollectionUtils.isEmpty(requests)) {
            log.info("No requests to update status to %s", status);
            return List.of();
        }
        requests.forEach(request -> request.setStatus(status));
        final List<Request> savedRequests = requestRepository.saveAll(requests);
        log.info("%s set to status %s", savedRequests.size(), status);
        return savedRequests;
    }
}
