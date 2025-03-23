package ru.practicum.ewm.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation mapToCompilation(final NewCompilationDto dto, final Set<Event> events) {
        if (dto == null) {
            return null;
        }
        final Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(dto.pinned());
        compilation.setTitle(dto.title());
        return compilation;
    }

    public CompilationDto mapToDto(final Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents() == null ? null :
                        compilation.getEvents().stream()
                                .map(event -> //TODO: запрашивать юзера через клиент
                                        eventMapper.mapToShortDto(event, new UserShortDto(1, "name")))
                                .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public List<CompilationDto> mapToDto(final List<Compilation> compilations) {
        if (compilations == null) {
            return null;
        }
        return compilations.stream().map(this::mapToDto).toList();
    }
}
