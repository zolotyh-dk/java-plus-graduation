package ru.practicum.ewm.stats;

import lombok.Builder;

@Builder(toBuilder = true)
public record ViewStatsDto(

        String app,
        String uri,
        Long hits) {

}
