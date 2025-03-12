package ru.practicum.ewm.category;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
class CategoryController extends HttpRequestResponseLogger {

    private final CategoryService categories;
    private final CategoryMapperImpl mapper;

    @GetMapping
    List<CategoryDto> get(
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final List<CategoryDto> dtos = mapper.mapToDto(categories.getAllInWindow(size, from / size));
        logHttpResponse(httpRequest, dtos);
        return dtos;
    }

    @GetMapping("/{id}")
    CategoryDto get(
            @PathVariable final long id,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        final CategoryDto dto = mapper.mapToDto(categories.getById(id));
        logHttpResponse(httpRequest, dto);
        return dto;
    }
}
