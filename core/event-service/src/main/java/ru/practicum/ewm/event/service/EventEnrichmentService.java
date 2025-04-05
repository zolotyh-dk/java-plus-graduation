package ru.practicum.ewm.event.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventPatch;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.client.RequestClient;
//import ru.practicum.ewm.stats.StatsClient;
//import ru.practicum.ewm.stats.ViewStatsDto;
import ru.practicum.ewm.stats.message.ActionTypeProto;
import ru.practicum.ewm.stats.message.UserActionProto;
import ru.practicum.ewm.stats.service.UserActionControllerGrpc;
import ru.practicum.ewm.user.client.UserClient;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventEnrichmentService {
    private static final LocalDateTime VIEWS_FROM = LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime VIEWS_TO = LocalDateTime.of(2100, Month.DECEMBER, 31, 23, 59, 59);

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserClient userClient;
    private final RequestClient requestClient;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorClient;

    public EventFullDto add(long userId, NewEventDto newEventDto) {
        checkUserExists(userId);
        Event event = eventMapper.mapToEvent(userId, newEventDto);
        Event savedEvent = eventService.add(event);
        fetchUser(savedEvent);
        return eventMapper.mapToFullDto(savedEvent);
    }

    public EventFullDto getPublishedById(long eventId, long userId) {
        Event event = eventService.getPublishedById(eventId, userId);
        fetchUser(event);
        fetchConfirmedRequests(event);
//        fetchViews(event);
        sendUserActionToCollector(eventId, userId);
        return eventMapper.mapToFullDto(event);
    }

    public EventFullDto getByIdAndUserId(long id, long userId) {
        Event event = eventService.getByIdAndUserId(id, userId);
        fetchUser(event);
        fetchConfirmedRequests(event);
//        fetchViews(event);
        return eventMapper.mapToFullDto(event);
    }

    public EventFullDto getById(long id) {
        Event event = eventService.getById(id);
        fetchUser(event);
        fetchConfirmedRequests(event);
//        fetchViews(event);
        return eventMapper.mapToFullDto(event);
    }

    public List<EventFullDto> getFullEvents(EventFilter filter) {
        List<Event> events = eventService.get(filter);
        fetchUsers(events);
        fetchConfirmedRequests(events);
//        fetchViews(events);
        return eventMapper.mapToFullDto(events);
    }

    public List<EventShortDto> getShortEvents(EventFilter filter) {
        List<Event> events = eventService.get(filter);
        fetchUsers(events);
        fetchConfirmedRequests(events);
//        fetchViews(events);
        return eventMapper.mapToShortDto(events);
    }

    public EventFullDto update(long id, UpdateEventAdminRequest updateEventAdminRequest) {
        EventPatch patch = eventMapper.mapToPatch(updateEventAdminRequest);
        Event event = eventService.update(id, patch);
        fetchUser(event);
        fetchConfirmedRequests(event);
//        fetchViews(event);
        return eventMapper.mapToFullDto(event);
    }

    public EventFullDto update(long eventId, UpdateEventUserRequest updateEventUserRequest, long userId) {
        final EventPatch patch = eventMapper.mapToPatch(updateEventUserRequest);
        Event event = eventService.update(eventId, patch, userId);
        fetchUser(event);
        fetchConfirmedRequests(event);
//        fetchViews(event);
        return eventMapper.mapToFullDto(event);
    }

    public boolean existsById(long eventId) {
        return eventService.existsById(eventId);
    }

    private void checkUserExists(long userId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }

    private void fetchUsers(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        log.debug("Fetching users from {}", userIds);
        List<UserShortDto> initiators = userClient.findAllByIdIn(userIds);
        Map<Long, UserShortDto> initiatorsMap = initiators.stream()
                .collect(Collectors.toMap(UserShortDto::id, Function.identity()));
        log.debug("Fetching users {}", initiatorsMap);
        events.forEach(event -> event.setInitiator(initiatorsMap.get(event.getInitiatorId())));
    }

    private void fetchUser(Event event) {
        fetchUsers(Collections.singletonList(event));
    }

    private void fetchConfirmedRequests(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).toList();
        Map<Long, Long> confirmedRequests = requestClient.getRequestStats(ids);
        events.forEach(event -> event
                .setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L)));
    }

    private void fetchConfirmedRequests(Event event) {
        fetchConfirmedRequests(Collections.singletonList(event));
    }

//    private void fetchViews(List<Event> events) {
//        List<Long> ids = events.stream().map(Event::getId).toList();
//        List<String> uris = ids.stream().map(id -> "/events/" + id).toList();
//        Map<String, Long> views = statsClient.getStats(VIEWS_FROM, VIEWS_TO, uris, true).stream()
//                .collect(Collectors.toMap(ViewStatsDto::uri, ViewStatsDto::hits));
//        events.forEach(event -> event
//                .setViews(views.getOrDefault("/events/" + event.getId(), 0L)));
//    }

//    private void fetchViews(Event event) {
//        fetchViews(Collections.singletonList(event));
//    }

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
                .setActionType(ActionTypeProto.ACTION_VIEW)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
    }
}
