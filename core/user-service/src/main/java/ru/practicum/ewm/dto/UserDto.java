package ru.practicum.ewm.dto;

import lombok.Builder;

@Builder
public record UserDto(
        long id,
        String email,
        String name) {
}
