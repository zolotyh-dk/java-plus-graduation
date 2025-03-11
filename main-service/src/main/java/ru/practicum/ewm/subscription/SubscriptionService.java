package ru.practicum.ewm.subscription;

import ru.practicum.ewm.event.EventFilter;
import ru.practicum.ewm.event.EventShortDto;

import java.util.List;

public interface SubscriptionService {
    void subscribe(long subscriberId, long targetId);

    List<EventShortDto> getEvents(long subscriberId, EventFilter filter);

    void unsubscribe(long subscriberId, long targetId);
}
