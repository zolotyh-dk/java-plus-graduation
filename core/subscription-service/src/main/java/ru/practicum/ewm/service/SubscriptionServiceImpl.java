package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotPossibleException;
import ru.practicum.ewm.model.Subscription;
import ru.practicum.ewm.repository.SubscriptionRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

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
    public List<Long> findTargetIdsBySubscriberId(long subscriberId) {
        return subscriptionRepository.findTargetIdsBySubscriberId(subscriberId);
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
