package ru.practicum.ewm.stats;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "statsClient")
public interface StatsClient {
    @PostMapping( "/hit")
    void saveHit(@RequestBody EndpointHitDto hitDto);

    @GetMapping( "/stats")
    List<ViewStatsDto> getStats(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                @RequestParam("uris") List<String> uris,
                                @RequestParam("unique") boolean unique);
}
