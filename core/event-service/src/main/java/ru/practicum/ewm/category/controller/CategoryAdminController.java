package ru.practicum.ewm.category.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryPatch;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.category.dto.CategoryUpdateDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.exception.HttpRequestResponseLogger;
import ru.practicum.ewm.category.dto.CategoryDto;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
class CategoryAdminController extends HttpRequestResponseLogger {
    private final CategoryService categories;
    private final CategoryMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto add(
            @RequestBody @Valid final CategoryCreateDto categoryCreateDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, categoryCreateDto);
        final Category category = mapper.mapToCategory(categoryCreateDto);
        final CategoryDto dto = mapper.mapToDto(categories.add(category));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @PatchMapping("/{id}")
    CategoryDto update(
            @PathVariable final long id,
            @RequestBody @Valid final CategoryUpdateDto categoryUpdateDto,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest, categoryUpdateDto);
        final CategoryPatch patch = mapper.mapToCategoryPatch(categoryUpdateDto);
        final CategoryDto dto = mapper.mapToDto(categories.update(id, patch));
        logHttpResponse(httpRequest, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remove(
            @PathVariable final long id,
            final HttpServletRequest httpRequest) {
        logHttpRequest(httpRequest);
        categories.removeById(id);
        logHttpResponse(httpRequest);
    }
}
