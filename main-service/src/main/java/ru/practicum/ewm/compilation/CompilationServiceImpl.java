package ru.practicum.ewm.compilation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationMapper mapper;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        final BooleanExpression byPinned = pinned != null
                ? QCompilation.compilation.pinned.eq(pinned)
                : Expressions.TRUE; // если pinned = null ищем все подборки без фильтрации
        final List<Compilation> compilations = compilationRepository.findAll(byPinned, pageable).getContent();
        return mapper.mapToDto(compilations);
    }

    @Override
    public CompilationDto getById(long id) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Compilation.class, id));
        return mapper.mapToDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto save(final NewCompilationDto requestDto) {
        final Compilation compilation = mapper.mapToCompilation(requestDto, fetchEvents(requestDto.events()));
        return mapper.mapToDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void delete(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(Compilation.class, id);
        }
        compilationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CompilationDto update(long id, UpdateCompilationRequest requestDto) {
        final Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Compilation.class, id));
        if (requestDto.title() != null) {
            compilation.setTitle(requestDto.title());
        }
        if (requestDto.pinned() != null) {
            compilation.setPinned(requestDto.pinned());
        }
        if (requestDto.events() != null) {
            compilation.setEvents(fetchEvents(requestDto.events()));
        }
        final Compilation updatedCompilation = compilationRepository.save(compilation);
        return mapper.mapToDto(updatedCompilation);
    }

    private Set<Event> fetchEvents(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Set.of();
        }
        final Set<Event> relatedEvents = eventRepository.findAllByIdIn(ids);
        if (ids.size() != relatedEvents.size()) {
            final Set<Long> foundEventIds = relatedEvents.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingEventIds = new HashSet<>(ids);
            missingEventIds.removeAll(foundEventIds);
            throw new NotFoundException(Event.class, missingEventIds);
        }
        return relatedEvents;
    }
}
