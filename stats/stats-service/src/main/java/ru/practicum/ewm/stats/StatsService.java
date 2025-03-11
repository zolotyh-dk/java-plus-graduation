package ru.practicum.ewm.stats;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addEndpointHit(@Valid EndpointHit endpointHit);

    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
