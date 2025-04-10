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
import ru.practicum.ewm.stats.message.*;
import ru.practicum.ewm.stats.service.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.service.UserActionControllerGrpc;
import ru.practicum.ewm.user.client.UserClient;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventEnrichmentService {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserClient userClient;
    private final RequestClient requestClient;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorClient;

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzerClient;

    public EventFullDto add(long userId, NewEventDto newEventDto) {
        checkUserExists(userId);
        Event event = eventMapper.mapToEvent(userId, newEventDto);
        Event savedEvent = eventService.add(event);
        fetchUser(savedEvent);
        fetchRatings(savedEvent);
        return eventMapper.mapToFullDto(savedEvent);
    }

    public EventFullDto getPublishedById(long eventId, long userId) {
        Event event = eventService.getPublishedById(eventId, userId);
        fetchUser(event);
        fetchConfirmedRequests(event);
        fetchRatings(event);
        sendUserActionToCollector(eventId, userId);
        return eventMapper.mapToFullDto(event);
    }

    public EventFullDto getByIdAndUserId(long id, long userId) {
        Event event = eventService.getByIdAndUserId(id, userId);
        fetchUser(event);
        fetchConfirmedRequests(event);
        fetchRatings(event);
        return eventMapper.mapToFullDto(event);
    }

    public EventFullDto getById(long id) {
        Event event = eventService.getById(id);
        fetchUser(event);
        fetchConfirmedRequests(event);
        fetchRatings(event);
        return eventMapper.mapToFullDto(event);
    }

    public List<EventFullDto> getFullEvents(EventFilter filter) {
        List<Event> events = eventService.get(filter);
        fetchUsers(events);
        fetchConfirmedRequests(events);
        fetchRatings(events);
        return eventMapper.mapToFullDto(events);
    }

    public List<EventShortDto> getShortEvents(EventFilter filter) {
        List<Event> events = eventService.get(filter);
        fetchUsers(events);
        fetchConfirmedRequests(events);
        fetchRatings(events);
        return eventMapper.mapToShortDto(events);
    }

    public EventFullDto update(long id, UpdateEventAdminRequest updateEventAdminRequest) {
        EventPatch patch = eventMapper.mapToPatch(updateEventAdminRequest);
        Event event = eventService.update(id, patch);
        fetchUser(event);
        fetchConfirmedRequests(event);
        fetchRatings(event);
        return eventMapper.mapToFullDto(event);
    }

    public EventFullDto update(long eventId, UpdateEventUserRequest updateEventUserRequest, long userId) {
        final EventPatch patch = eventMapper.mapToPatch(updateEventUserRequest);
        Event event = eventService.update(eventId, patch, userId);
        fetchUser(event);
        fetchConfirmedRequests(event);
        fetchRatings(event);
        return eventMapper.mapToFullDto(event);
    }

    public List<EventShortDto> getRecommendationsForUser(long userId, int maxResults) {
        checkUserExists(userId);
        UserPredictionsRequestProto requestProto = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        Iterator<RecommendedEventProto> iterator = analyzerClient.getRecommendationsForUser(requestProto);
        List<Long> eventIds = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(RecommendedEventProto::getEventId)
                .toList();
        log.debug("Received recommended eventIds from analyzer: {}", eventIds);
        List<Event> events = eventService.getAvailableUpcomingEventsByIds(eventIds);
        fetchUsers(events);
        fetchConfirmedRequests(events);
        fetchRatings(events);
        return eventMapper.mapToShortDto(events);
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

    private void fetchRatings(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        log.debug("Fetching ratings to events: {}", eventIds);
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        // На основе примера в ТЗ "Обработка потока сообщений":
        // gRPC потоковый ответ в виде Iterator
        Iterator<RecommendedEventProto> iterator = analyzerClient.getInteractionsCount(request);

        // Преобразуем iterator -> stream -> Map<eventId, score>
        Map<Long, Double> eventsRatings = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false
        ).collect(Collectors.toMap(
                RecommendedEventProto::getEventId,
                RecommendedEventProto::getScore
        ));
        log.debug("Fetching ratings for events: {}", eventsRatings);
        events.forEach(event -> event.setRating(eventsRatings.getOrDefault(event.getId(), 0.0)));
    }

    private void fetchRatings(Event event) {
        fetchRatings(Collections.singletonList(event));
    }

    private void sendUserActionToCollector(final long eventId, final long userId) {
        final UserActionProto userActionProto = createUserActionProto(eventId, userId);
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
