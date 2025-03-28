package ru.practicum.ewm.category.mapper;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryMapper {

    Category mapToCategory(Long id);

    CategoryDto mapToDto(Category category);

    List<CategoryDto> mapToDto(List<Category> categories);
}
