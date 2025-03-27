package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventEnrichmentService;

import java.util.List;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
class EventInternalController extends HttpRequestResponseLogger {
    private final EventEnrichmentService eventService;

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable("eventId") Long eventId, HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        EventFullDto dto = eventService.getById(eventId);
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping("/{eventId}/exists")
    boolean existsById(@PathVariable("eventId") Long eventId, HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final boolean isExists = eventService.existsById(eventId);
        logHttpResponse(httpRequest, isExists);
        return isExists;
    }

    @PostMapping
    List<EventShortDto> get(@RequestBody EventFilter eventFilter, HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        List<EventShortDto> dtos = eventService.getShortEvents(eventFilter);
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }
}