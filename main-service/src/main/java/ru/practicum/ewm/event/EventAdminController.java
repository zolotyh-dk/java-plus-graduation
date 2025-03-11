package ru.practicum.ewm.event;

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
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
class EventAdminController extends HttpRequestResponseLogger {

    private final EventService events;
    private final EventMapper mapper;
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
        final EventPatch patch = mapper.mapToPatch(updateEventAdminRequest);
        final EventFullDto dto = mapper.mapToFullDto(events.update(eventId, patch));
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
        final List<EventFullDto> dtos = mapper.mapToFullDto(events.get(filter));
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }
}
