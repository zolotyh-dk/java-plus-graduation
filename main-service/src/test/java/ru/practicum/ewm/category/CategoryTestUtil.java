package ru.practicum.ewm.category;

public class CategoryTestUtil {
    public static final long CATEGORY_ID_1 = 1L;
    public static final String CATEGORY_NAME_1 = "Название категории 1";

    public static final CategoryDto CATEGORY_DTO_1 = CategoryDto.builder()
            .id(CATEGORY_ID_1)
            .name(CATEGORY_NAME_1)
            .build();
}
