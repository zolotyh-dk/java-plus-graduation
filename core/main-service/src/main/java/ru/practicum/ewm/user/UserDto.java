package ru.practicum.ewm.user;

import lombok.Builder;

@Builder
public record UserDto(
        long id,
        String email,
        String name) {
}
