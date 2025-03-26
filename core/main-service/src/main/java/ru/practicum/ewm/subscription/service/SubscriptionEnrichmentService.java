package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.client.UserClient;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionEnrichmentService {
    private final SubscriptionService subscriptionService;
    private final UserClient userClient;
    private final EventMapper eventMapper;

    public void subscribe(long subscriberId, long targetId) {
        checkUserExists(subscriberId);
        checkUserExists(targetId);
        subscriptionService.subscribe(subscriberId, targetId);
    }

    public List<EventShortDto> getEvents(long subscriberId, EventFilter filter) {
        checkUserExists(subscriberId);
        List<Event> events = subscriptionService.getEvents(subscriberId, filter);
        List<UserShortDto> initiators = findUsers(events);
        return eventMapper.mapToShortDto(events);
    }

    public void unsubscribe(long subscriberId, long targetId) {
        checkUserExists(subscriberId);
        checkUserExists(targetId);
        subscriptionService.unsubscribe(subscriberId, targetId);
    }

    private void checkUserExists(long userId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
    }

    private List<UserShortDto> findUsers(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        return userClient.findAllByIdIn(userIds);
    }
}
