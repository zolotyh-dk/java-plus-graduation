package ru.practicum.ewm.user.dto;

import lombok.Builder;

@Builder
public record UserShortDto(
        long id,
        String name) {
}
