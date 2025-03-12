package ru.practicum.ewm.user;

import lombok.Builder;

@Builder
public record UserShortDto(
        long id,
        String name) {
}
