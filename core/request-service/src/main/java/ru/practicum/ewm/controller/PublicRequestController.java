package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.HttpRequestResponseLogger;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.service.RequestEnrichmentService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PublicRequestController extends HttpRequestResponseLogger {
    private final RequestEnrichmentService facade;

    @GetMapping
    Collection<RequestDto> get(@PathVariable final long userId, final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        Collection<RequestDto> response = facade.getAllRequestByUserId(userId);
        logHttpResponse(httpRequest, response);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto save(@PathVariable final long userId, @RequestParam long eventId, final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final RequestDto requestDto = facade.create(userId, eventId);
        logHttpResponse(httpRequest, requestDto);
        return requestDto;
    }

    @PatchMapping("/{requestId}/cancel")
    RequestDto delete(@PathVariable final long userId, @PathVariable long requestId, final HttpServletRequest request) {
        logHttpRequest(request);
        RequestDto requestDto = facade.cancel(userId, requestId);
        logHttpResponse(request, requestDto);
        return requestDto;
    }
}