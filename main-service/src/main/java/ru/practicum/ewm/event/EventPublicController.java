package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import ru.practicum.ewm.stats.EndpointHitDto;
import ru.practicum.ewm.stats.StatsClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
class EventPublicController extends HttpRequestResponseLogger {

    private static final String APP = "main-service";

    private final EventService events;
    private final EventMapper mapper;
    private final StatsClient statsClient;
    private final Clock clock;

    @GetMapping("{eventId}")
    EventFullDto get(
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.getPublishedById(eventId));
        statsClient.saveHit(new EndpointHitDto(APP, httpRequest.getRequestURI(), httpRequest.getRemoteAddr(),
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS)));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping
    List<EventShortDto> get(
            @RequestParam(required = false) final String text,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") final boolean onlyAvailable,
            @RequestParam(required = false) final EventSort sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
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
        final List<EventShortDto> dtos = mapper.mapToDto(events.get(filter));
        statsClient.saveHit(new EndpointHitDto(APP, httpRequest.getRequestURI(), httpRequest.getRemoteAddr(),
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS)));
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }
}
