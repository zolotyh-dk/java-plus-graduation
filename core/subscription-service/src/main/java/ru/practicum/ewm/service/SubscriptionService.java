package ru.practicum.ewm.service;

import java.util.List;

public interface SubscriptionService {
    void subscribe(long subscriberId, long targetId);

    List<Long> findTargetIdsBySubscriberId(long subscriberId);

    void unsubscribe(long subscriberId, long targetId);
}
