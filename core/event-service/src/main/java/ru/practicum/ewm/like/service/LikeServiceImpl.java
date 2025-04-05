package ru.practicum.ewm.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final EventService eventService;

    @Override
    public void add(long eventId, long userId) {
        Event event = eventService.getById(eventId);
    }
}
