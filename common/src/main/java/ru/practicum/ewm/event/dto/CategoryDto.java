package ru.practicum.ewm.event.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record CategoryDto(

        Long id,
        String name) {

}
