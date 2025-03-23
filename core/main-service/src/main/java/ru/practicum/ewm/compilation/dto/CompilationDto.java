package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@Builder
public record CompilationDto(
        Set<EventShortDto> events,
        Long id,
        boolean pinned,
        String title) {
}
