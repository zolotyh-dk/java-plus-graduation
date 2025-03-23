package ru.practicum.ewm.subscription.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventSort;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.subscription.service.SubscriptionEnrichmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionPrivateController extends HttpRequestResponseLogger {
    private final SubscriptionEnrichmentService facade;

    @PostMapping
    public void subscribe(@PathVariable final long userId,
                          @RequestParam final long initiatorId,
                          final HttpServletRequest request) {
        logHttpRequest(request);
        facade.subscribe(userId, initiatorId);
        logHttpResponse(request);
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @PathVariable final long userId,
            @RequestParam(required = false) final String text,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") final boolean onlyAvailable,
            @RequestParam(required = false) final EventSort sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request) {
        final EventFilter filter = EventFilter.builder()
                .states(List.of(EventState.PUBLISHED))
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
        final List<EventShortDto> dtos = facade.getEvents(userId, filter);
        logHttpResponse(request, dtos);
        return dtos;
    }

    @DeleteMapping("/{initiatorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable final long userId,
                            @PathVariable final long initiatorId,
                            final HttpServletRequest request) {
        logHttpRequest(request);
        facade.unsubscribe(userId, initiatorId);
        logHttpResponse(request);
    }
}
