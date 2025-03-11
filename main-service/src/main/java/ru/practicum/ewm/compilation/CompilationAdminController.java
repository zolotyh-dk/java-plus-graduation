package ru.practicum.ewm.compilation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController extends HttpRequestResponseLogger {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid final NewCompilationDto requestDto,
                               final HttpServletRequest request) {
        logHttpRequest(request, requestDto);
        final CompilationDto responseDto = compilationService.save(requestDto);
        logHttpResponse(request, responseDto);
        return responseDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final long id, final HttpServletRequest request) {
        logHttpRequest(request);
        compilationService.delete(id);
        logHttpResponse(request);
    }

    @PatchMapping("/{id}")
    public CompilationDto update(@PathVariable final long id,
                       @RequestBody @Valid final UpdateCompilationRequest requestDto,
                       final HttpServletRequest request) {
        logHttpRequest(request, requestDto);
        final CompilationDto responseDto = compilationService.update(id, requestDto);
        logHttpResponse(request, responseDto);
        return responseDto;
    }
}
