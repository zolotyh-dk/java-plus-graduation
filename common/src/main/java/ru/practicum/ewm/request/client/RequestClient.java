package ru.practicum.ewm.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.request.dto.RequestState;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service")
public interface RequestClient {
    @GetMapping
    Map<Long, Long> getRequestStats(@RequestParam List<Long> eventIds);
}
