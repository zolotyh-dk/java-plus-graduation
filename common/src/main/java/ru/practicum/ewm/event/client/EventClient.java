package ru.practicum.ewm.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@FeignClient(name = "main-service")
public interface EventClient {
    @GetMapping("internal/events/{eventId}")
    EventFullDto getById(@PathVariable long eventId);

    @PostMapping("internal/events")
    List<EventShortDto> get(@RequestBody EventFilter filter);

    @GetMapping("/internal/events/{eventId}/exists")
    boolean existsById(@PathVariable long eventId);
}
