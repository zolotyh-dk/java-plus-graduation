package ru.practicum.ewm.compilation;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto getById(long id);

    CompilationDto save(NewCompilationDto requestDto);

    void delete(long id);

    CompilationDto update(long id, UpdateCompilationRequest requestDto);
}
