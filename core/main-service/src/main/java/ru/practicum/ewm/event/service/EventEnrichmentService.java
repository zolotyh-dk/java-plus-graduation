package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.client.RequestClient;
import ru.practicum.ewm.stats.ViewStatsDto;
import ru.practicum.ewm.user.client.UserClient;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventEnrichmentService {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserClient userClient;
    private final RequestClient requestClient;

    public EventFullDto add(long userId, NewEventDto newEventDto) {
        checkUserExists(userId);
        Event event = eventMapper.mapToEvent(userId, newEventDto);
        Event savedEvent = eventService.add(event);
        UserShortDto initiator = findUser(savedEvent);
        return eventMapper.mapToFullDto(savedEvent, initiator);
    }

    public EventFullDto getPublishedById(long eventId) {
        Event event = eventService.getPublishedById(eventId);
        UserShortDto initiator = findUser(event);
        return eventMapper.mapToFullDto(event, initiator);
    }

    public EventFullDto getByIdAndUserId(long id, long userId) {
        Event event = eventService.getByIdAndUserId(id, userId);
        UserShortDto initiator = findUser(event);
        return eventMapper.mapToFullDto(event, initiator);
    }

    public List<EventFullDto> getFullEvents(EventFilter filter) {
        List<Event> events = eventService.get(filter);
        Map<Long, Long> confirmedRequestStats = getConfirmedRequests(events);
        events.forEach(event -> event
                .setConfirmedRequests(confirmedRequestStats.getOrDefault(event.getId(), 0L)));
        List<UserShortDto> initiators = findUsers(events);
        return eventMapper.mapToFullDto(events, initiators);
    }

    public List<EventShortDto> getShortEvents(EventFilter filter) {
        List<Event> events = eventService.get(filter);
        List<UserShortDto> initiators = findUsers(events);
        return eventMapper.mapToShortDto(events, initiators);
    }

    public EventFullDto update(long id, UpdateEventAdminRequest updateEventAdminRequest) {
        EventPatch patch = eventMapper.mapToPatch(updateEventAdminRequest);
        Event event = eventService.update(id, patch);
        UserShortDto initiator = findUser(event);
        return eventMapper.mapToFullDto(event, initiator);
    }

    public EventFullDto update(long eventId, UpdateEventUserRequest updateEventUserRequest, long userId) {
        final EventPatch patch = eventMapper.mapToPatch(updateEventUserRequest);
        Event event = eventService.update(eventId, patch, userId);
        UserShortDto initiator = findUser(event);
        return eventMapper.mapToFullDto(event, initiator);
    }

    public boolean existsByIdAndUserId(long userId, long eventId) {
        return eventService.existsByIdAndUserId(userId, eventId);
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

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).toList();
        return requestClient.getRequestStats(ids);
    }

    private Long getConfirmedRequests(final Event event) {
        getConfirmedRequests(List.of(event));
        return event;
    }

    private Map<Long, Long> getHits(final List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).toList();
        List<String> uris = ids.stream().map(id -> "/events/" + id).toList();
        final Map<String, Long> views = statsClient.getStats(VIEWS_FROM, VIEWS_TO, uris, true).stream()
                .collect(Collectors.toMap(ViewStatsDto::uri, ViewStatsDto::hits));
        events.forEach(event -> event.setViews(views.getOrDefault("/events/" + event.getId(), 0L)));
    }
}
