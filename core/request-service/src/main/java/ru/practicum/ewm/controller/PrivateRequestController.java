package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.HttpRequestResponseLogger;
import ru.practicum.ewm.request.dto.EventRequestStatusDto;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.UpdateEventRequestStatusDto;
import ru.practicum.ewm.service.RequestEnrichmentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
class PrivateRequestController extends HttpRequestResponseLogger {
    private final RequestEnrichmentService facade;
    private final RequestDtoValidatorExtension requestDtoValidatorExtension;

    @InitBinder
    void initBinder(final WebDataBinder binder) {
        binder.addValidators(requestDtoValidatorExtension);
    }

    @GetMapping
    List<RequestDto> getRequests(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final List<RequestDto> dtos = facade.getRequests(userId, eventId);
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }

    @PatchMapping
    EventRequestStatusDto processRequests(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            @RequestBody @Valid final UpdateEventRequestStatusDto updateDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, updateDto);
        final EventRequestStatusDto dto = facade.processRequests(eventId, updateDto, userId);
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
