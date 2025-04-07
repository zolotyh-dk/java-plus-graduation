package ru.practicum.ewm.like.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.stats.message.ActionTypeProto;
import ru.practicum.ewm.stats.message.UserActionProto;
import ru.practicum.ewm.stats.service.UserActionControllerGrpc;
import ru.practicum.ewm.user.client.UserClient;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeEnrichmentService {
    private final UserClient userClient;
    private final EventService eventService;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorClient;

    public void add(long eventId, long userId) {
        checkUserExists(userId);
        checkEventExists(eventId);
        sendUserActionToCollector(eventId, userId);
    }

    private void checkUserExists(long userId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }

    private void checkEventExists(long eventId) {
        if (!eventService.existsById(eventId)) {
            throw new NotFoundException("Event", eventId);
        }
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
                .setActionType(ActionTypeProto.ACTION_LIKE)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
    }
}
