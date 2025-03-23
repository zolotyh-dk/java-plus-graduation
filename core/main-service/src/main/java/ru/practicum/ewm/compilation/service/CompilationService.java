package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto getById(long id);

    CompilationDto save(NewCompilationDto requestDto);

    void delete(long id);

    CompilationDto update(long id, UpdateCompilationRequest requestDto);
}
