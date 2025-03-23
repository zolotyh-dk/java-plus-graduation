package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.user.client.UserClient;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserClient userClient;

    @Transactional
    @Override
    public void subscribe(long subscriberId, long targetId) {
        if (subscriberId == targetId) {
            throw new NotPossibleException("User cannot subscribe to himself");
        }
        if (subscriptionRepository.existsBySubscriberIdAndTargetId(subscriberId, targetId)) {
            throw new NotPossibleException("Subscription already exists");
        }
        final Subscription subscription = new Subscription();
        subscription.setSubscriberId(subscriberId);
        subscription.setTargetId(targetId);
        subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEvents(long subscriberId, EventFilter filter) {
        final List<Long> initiatorIds = subscriptionRepository.findTargetIdsBySubscriberId(subscriberId);
        final EventFilter filterWithInitiators = filter.toBuilder().users(initiatorIds).build();
        return eventService.get(filterWithInitiators);
    }

    @Transactional
    @Override
    public void unsubscribe(long subscriberId, long targetId) {
        if (subscriberId == targetId) {
            throw new NotPossibleException("User cannot unsubscribe from himself");
        }
        final Subscription subscription = subscriptionRepository.findBySubscriberIdAndTargetId(subscriberId, targetId)
                .orElseThrow(() -> new NotFoundException(Subscription.class, Set.of(subscriberId, targetId)));
        subscriptionRepository.delete(subscription);
    }
}
