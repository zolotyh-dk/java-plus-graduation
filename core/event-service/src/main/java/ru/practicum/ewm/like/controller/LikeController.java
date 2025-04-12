package ru.practicum.ewm.like.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.like.service.LikeEnrichmentService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events/{eventId}/like")
public class LikeController {
    private final LikeEnrichmentService likeEnrichmentService;

    @PutMapping
    public void add(@PathVariable long eventId, @RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("Received new like: eventId: {}, userId: {}", eventId, userId);
        likeEnrichmentService.add(eventId, userId);
        log.info("Added new like: eventId: {}, userId: {}", eventId, userId);
    }
}
