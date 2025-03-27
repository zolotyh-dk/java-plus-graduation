package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.client.EventClient;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.client.UserClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionEnrichmentService {
    private final SubscriptionService subscriptionService;
    private final UserClient userClient;
    private final EventClient eventClient;

    public void subscribe(long subscriberId, long targetId) {
        checkUserExists(subscriberId);
        checkUserExists(targetId);
        subscriptionService.subscribe(subscriberId, targetId);
    }

    public List<EventShortDto> getEvents(long subscriberId, EventFilter filter) {
        checkUserExists(subscriberId);
        List<Long> initiatorIds = subscriptionService.findTargetIdsBySubscriberId(subscriberId);
        final EventFilter filterWithInitiators = filter.toBuilder().users(initiatorIds).build();
        return eventClient.get(filterWithInitiators);
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
}
