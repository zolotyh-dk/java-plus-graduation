package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.exception.HttpRequestResponseLogger;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventEnrichmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
class EventAdminController extends HttpRequestResponseLogger {
    private final EventEnrichmentService eventEnrichmentService;
    private final EventDtoValidatorExtension eventDtoValidatorExtension;

    @InitBinder
    void initBinder(final WebDataBinder binder) {
        binder.addValidators(eventDtoValidatorExtension);
    }

    @PatchMapping("/{eventId}")
    EventFullDto update(
            @PathVariable final long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, updateEventAdminRequest);
        final EventFullDto dto = eventEnrichmentService.update(eventId, updateEventAdminRequest);
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping
    List<EventFullDto> get(
            @RequestParam(required = false) final List<Long> users,
            @RequestParam(required = false) final List<EventState> states,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") final LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final EventFilter filter = EventFilter.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        final List<EventFullDto> dtos = eventEnrichmentService.getFullEvents(filter);
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }
}
