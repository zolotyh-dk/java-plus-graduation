package ru.practicum.ewm.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final Clock clock;
    private final CategoryMapper categoryMapper;

    public Event mapToEvent(final Long userId, final NewEventDto dto) {
        if (userId == null && dto == null) {
            return null;
        }
        final Event event = new Event();
        event.setInitiatorId(userId);
        if (dto != null) {
            event.setTitle(dto.title());
            event.setCategory(categoryMapper.mapToCategory(dto.category()));
            event.setEventDate(dto.eventDate());
            event.setLocation(copyLocation(dto.location()));
            event.setAnnotation(dto.annotation());
            event.setDescription(dto.description());
            event.setParticipantLimit(dto.participantLimit() == null ? 0L : dto.participantLimit());
            event.setPaid(Boolean.TRUE.equals(dto.paid()));
            event.setRequestModeration(dto.requestModeration() == null || dto.requestModeration());
        }
        event.setCreatedOn(LocalDateTime.now(clock));
        event.setState(EventState.PENDING);
        return event;
    }

    public EventPatch mapToPatch(final UpdateEventAdminRequest dto) {
        if (dto == null) {
            return null;
        }
        return EventPatch.builder()
                .title(dto.title())
                .category(categoryMapper.mapToCategory(dto.category()))
                .eventDate(dto.eventDate())
                .location(copyLocation(dto.location()))
                .annotation(dto.annotation())
                .description(dto.description())
                .participantLimit(dto.participantLimit())
                .paid(dto.paid())
                .requestModeration(dto.requestModeration())
                .state(
                        switch (dto.stateAction()) {
                            case PUBLISH_EVENT -> EventState.PUBLISHED;
                            case REJECT_EVENT -> EventState.CANCELED;
                            case null -> null;
                        }
                )
                .build();
    }

    public EventPatch mapToPatch(final UpdateEventUserRequest dto) {
        if (dto == null) {
            return null;
        }
        return EventPatch.builder()
                .title(dto.title())
                .category(categoryMapper.mapToCategory(dto.category()))
                .eventDate(dto.eventDate())
                .location(copyLocation(dto.location()))
                .annotation(dto.annotation())
                .description(dto.description())
                .participantLimit(dto.participantLimit())
                .paid(dto.paid())
                .requestModeration(dto.requestModeration())
                .state(
                        switch (dto.stateAction()) {
                            case SEND_TO_REVIEW -> EventState.PENDING;
                            case CANCEL_REVIEW -> EventState.CANCELED;
                            case null -> null;
                        }
                )
                .build();
    }

    public EventFullDto mapToFullDto(final Event event) {
        if (event == null) {
            return null;
        }
        return EventFullDto.builder()
                .id(event.getId())
                .initiator(event.getInitiator())
                .title(event.getTitle())
                .category(categoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .location(copyLocation(event.getLocation()))
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .paid(event.isPaid())
                .requestModeration(event.isRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .build();
    }

    public List<EventFullDto> mapToFullDto(final List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(this::mapToFullDto)
                .toList();
    }

    public EventShortDto mapToShortDto(final Event event) {
        if (event == null) {
            return null;
        }
        return EventShortDto.builder()
                .id(event.getId())
                .initiator(event.getInitiator())
                .title(event.getTitle())
                .category(categoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .paid(event.isPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }

    public List<EventShortDto> mapToShortDto(final List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(this::mapToShortDto)
                .toList();
    }

    private Location copyLocation(final Location location) {
        if (location == null) {
            return null;
        }
        final Location copy = new Location();
        copy.setLat(location.getLat());
        copy.setLon(location.getLon());
        return copy;
    }
}
