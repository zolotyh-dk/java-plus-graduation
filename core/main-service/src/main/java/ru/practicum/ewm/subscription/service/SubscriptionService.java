package ru.practicum.ewm.subscription.service;

import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface SubscriptionService {
    void subscribe(long subscriberId, long targetId);

    List<Event> getEvents(long subscriberId, EventFilter filter);

    void unsubscribe(long subscriberId, long targetId);
}
