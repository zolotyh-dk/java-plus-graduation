package ru.practicum.ewm.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.request.dto.EventRequestStatusDto;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.UpdateEventRequestStatusDto;
import ru.practicum.ewm.event.client.EventClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.stats.message.ActionTypeProto;
import ru.practicum.ewm.stats.message.UserActionProto;
import ru.practicum.ewm.stats.service.UserActionControllerGrpc;
import ru.practicum.ewm.user.client.UserClient;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestEnrichmentService {
    private final RequestService requestService;
    private final RequestMapper requestMapper;
    private final UserClient userClient;
    private final EventClient eventClient;

    @GrpcClient("collector")
    private final UserActionControllerGrpc.UserActionControllerBlockingStub collectorClient;

    public RequestDto create(long userId, long eventId) {
        checkUserExists(userId);
        EventFullDto event = getEvent(eventId);
        RequestDto requestDto = requestMapper.mapToRequestDto(requestService.create(userId, event));
        sendUserActionToCollector(event.id(), userId);
        return requestDto;
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
        checkEventExists(eventId);
        return requestMapper.mapToRequestDto(requestService.getRequests(userId, eventId));
    }

    public EventRequestStatusDto processRequests(long eventId, UpdateEventRequestStatusDto updateDto, long userId) {
        EventFullDto event = getEvent(eventId);
        Pair<List<Request>, List<Request>> confirmedAndRejectedRequests =
                requestService.processRequests(event, updateDto, userId);
        return new EventRequestStatusDto(requestMapper.mapToRequestDto(confirmedAndRejectedRequests.getFirst()),
                requestMapper.mapToRequestDto(confirmedAndRejectedRequests.getSecond()));
    }

    private void checkUserExists(long userId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }

    private void checkEventExists(long eventId) {
        if (!eventClient.existsById(eventId)) {
            throw new NotFoundException("Event", eventId);
        }
    }

    private EventFullDto getEvent(long eventId) {
        return eventClient.getById(eventId);
    }

    private void sendUserActionToCollector(final long eventId, final long userId) {
        final UserActionProto userActionProto = createUserActionProto(userId, eventId);
        log.info("Send user action to collector: userId = {}, eventId = {}, actionType = {}, timestamp = {}",
                userActionProto.getUserId(),
                userActionProto.getEventId(),
                userActionProto.getActionType(),
                userActionProto.getTimestamp());
        collectorClient.collectUserAction(userActionProto);
    }

    private UserActionProto createUserActionProto(final long eventId, final long userId) {
        final Instant now = Instant.now();
        return UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_REGISTER)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
    }
}
