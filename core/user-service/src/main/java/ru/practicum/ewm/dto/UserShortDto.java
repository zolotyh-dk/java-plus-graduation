package ru.practicum.ewm.dto;

import lombok.Builder;

@Builder
public record UserShortDto(
        long id,
        String name) {
}
