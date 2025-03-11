package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryMapper {

    Category mapToCategory(Long id);

    CategoryDto mapToDto(Category category);

    List<CategoryDto> mapToDto(List<Category> categories);
}
