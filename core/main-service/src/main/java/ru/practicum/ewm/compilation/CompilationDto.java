package ru.practicum.ewm.compilation;

import lombok.Builder;
import ru.practicum.ewm.event.EventShortDto;

import java.util.Set;

@Builder
public record CompilationDto(
        Set<EventShortDto> events,
        Long id,
        boolean pinned,
        String title) {
}
