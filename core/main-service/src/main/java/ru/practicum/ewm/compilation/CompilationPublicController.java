package ru.practicum.ewm.compilation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController extends HttpRequestResponseLogger {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(required = false) Boolean pinned,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                    @RequestParam(defaultValue = "10") @Positive final int size,
                                    final HttpServletRequest request) {
        logHttpRequest(request);
        final PageRequest pageRequest = PageRequest.of(from / size, size);
        final List<CompilationDto> response = compilationService.getAll(pinned, pageRequest);
        logHttpResponse(request, response);
        return response;
    }

    @GetMapping("/{id}")
    public CompilationDto getById(@PathVariable final long id, final HttpServletRequest request) {
        logHttpRequest(request);
        final CompilationDto responseDto = compilationService.getById(id);
        logHttpResponse(request, responseDto);
        return responseDto;
    }
}
