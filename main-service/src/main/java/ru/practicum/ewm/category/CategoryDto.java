package ru.practicum.ewm.category;

import lombok.Builder;

@Builder(toBuilder = true)
public record CategoryDto(

        Long id,
        String name) {

}
