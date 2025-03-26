package ru.practicum.ewm.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.event.dto.EventFullDto;

@FeignClient(name = "main-service")
public interface EventClient {
    @GetMapping("internal/events/{eventId}")
    EventFullDto getById(@PathVariable long eventId);

    @GetMapping("/users/{userId}/events/exists/{eventId}")
    boolean existsById(@PathVariable long userId, @PathVariable long eventId);
}
