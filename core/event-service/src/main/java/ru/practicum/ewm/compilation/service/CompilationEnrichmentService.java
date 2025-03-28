package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.user.client.UserClient;

@Service
@RequiredArgsConstructor
public class CompilationEnrichmentService {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;
    private final UserClient userClient;
}
