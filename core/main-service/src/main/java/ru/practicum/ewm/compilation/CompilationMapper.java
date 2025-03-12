package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    Compilation mapToCompilation(final NewCompilationDto dto, final Set<Event> events) {
        if (dto == null) {
            return null;
        }
        final Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(dto.pinned());
        compilation.setTitle(dto.title());
        return compilation;
    }

    CompilationDto mapToDto(final Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents() == null ? null :
                        compilation.getEvents().stream().map(eventMapper::mapToDto).collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    List<CompilationDto> mapToDto(final List<Compilation> compilations) {
        if (compilations == null) {
            return null;
        }
        return compilations.stream().map(this::mapToDto).toList();
    }
}
