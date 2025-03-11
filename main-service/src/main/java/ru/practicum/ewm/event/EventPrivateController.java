package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
class EventPrivateController extends HttpRequestResponseLogger {

    private final EventService events;
    private final EventMapper mapper;
    private final EventDtoValidatorExtension eventDtoValidatorExtension;

    @InitBinder
    void initBinder(final WebDataBinder binder) {
        binder.addValidators(eventDtoValidatorExtension);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto add(
            @PathVariable final long userId,
            @RequestBody @Valid final NewEventDto newEventDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, newEventDto);
        final Event event = mapper.mapToEvent(userId, newEventDto);
        final EventFullDto dto = mapper.mapToFullDto(events.add(event));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping("{eventId}")
    EventFullDto get(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.getByIdAndUserId(eventId, userId));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @GetMapping
    List<EventShortDto> get(
            @PathVariable final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final EventFilter filter = EventFilter.builder()
                .users(List.of(userId))
                .from(from)
                .size(size)
                .build();
        final List<EventShortDto> dtos = mapper.mapToDto(events.get(filter));
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }

    @PatchMapping("/{eventId}")
    EventFullDto update(
            @PathVariable final long userId,
            @PathVariable final long eventId,
            @RequestBody @Valid final UpdateEventUserRequest updateEventUserRequest,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, updateEventUserRequest);
        final EventPatch patch = mapper.mapToPatch(updateEventUserRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.update(eventId, patch, userId));
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
