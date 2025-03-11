package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;
import ru.practicum.ewm.request.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
class EventRequestController extends HttpRequestResponseLogger {

    private final EventService events;
    private final EventRequestDtoValidatorExtension eventRequestDtoValidatorExtension;

    @InitBinder
    void initBinder(final WebDataBinder binder) {
        binder.addValidators(eventRequestDtoValidatorExtension);
    }

    @GetMapping
    List<RequestDto> getRequests(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final List<RequestDto> dtos = events.getRequests(userId, eventId);
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
        final EventRequestStatusDto dto = events.processRequests(eventId, updateDto, userId);
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
