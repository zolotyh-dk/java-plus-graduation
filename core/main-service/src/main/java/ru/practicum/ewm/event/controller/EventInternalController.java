package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.service.EventService;

@RestController
@RequestMapping("/internal/events/{eventId}")
@RequiredArgsConstructor
class EventInternalController extends HttpRequestResponseLogger {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping()
    EventFullDto getById(@PathVariable("eventId") Long eventId, final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        EventFullDto dto = eventMapper.mapToFullDto(eventService.getById(eventId));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping("/exists")
    boolean existsById(@PathVariable("eventId") Long eventId, final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final boolean isExists = eventService.existsById(eventId);
        logHttpResponse(httpRequest, isExists);
        return isExists;
    }
}
