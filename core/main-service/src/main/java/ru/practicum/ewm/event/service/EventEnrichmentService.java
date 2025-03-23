package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.client.UserClient;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventEnrichmentService {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserClient userClient;

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
