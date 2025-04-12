package ru.practicum.ewm.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service")
public interface RequestClient {
    @PostMapping("/internal/requests/stats")
    Map<Long, Long> getRequestStats(@RequestBody List<Long> eventIds);

    @GetMapping("/internal/requests/exist")
    boolean existsConfirmedParticipation(@RequestParam long userId, @RequestParam long eventId);
}
