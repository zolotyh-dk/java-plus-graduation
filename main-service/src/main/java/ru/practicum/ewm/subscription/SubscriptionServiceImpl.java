package ru.practicum.ewm.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public void subscribe(long subscriberId, long targetId) {
        if (subscriberId == targetId) {
            throw new NotPossibleException("User cannot subscribe to himself");
        }
        if (subscriptionRepository.existsBySubscriberIdAndTargetId(subscriberId, targetId)) {
            throw new NotPossibleException("Subscription already exists");
        }
        final User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new NotFoundException(User.class, subscriberId));
        final User target = userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException(User.class, targetId));
        final Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setTarget(target);
        subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEvents(long subscriberId, EventFilter filter) {
        if (!userRepository.existsById(subscriberId)) {
            throw new NotFoundException(User.class, subscriberId);
        }
        final List<Long> initiatorIds = subscriptionRepository.findTargetIdsBySubscriberId(subscriberId);
        final EventFilter filterWithInitiators = filter.toBuilder().users(initiatorIds).build();
        final List<Event> events = eventService.get(filterWithInitiators);
        return eventMapper.mapToDto(events);
    }

    @Transactional
    @Override
    public void unsubscribe(long subscriberId, long targetId) {
        if (subscriberId == targetId) {
            throw new NotPossibleException("User cannot unsubscribe from himself");
        }
        if (!userRepository.existsById(subscriberId)) {
            throw new NotFoundException(User.class, subscriberId);
        }
        if (!userRepository.existsById(targetId)) {
            throw new NotFoundException(User.class, targetId);
        }
        final Subscription subscription = subscriptionRepository.findBySubscriberIdAndTargetId(subscriberId, targetId)
                .orElseThrow(() -> new NotFoundException(Subscription.class, Set.of(subscriberId, targetId)));
        subscriptionRepository.delete(subscription);
    }
}
