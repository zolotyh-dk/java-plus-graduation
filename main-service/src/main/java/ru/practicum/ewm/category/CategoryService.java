package ru.practicum.ewm.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public interface CategoryService {

    Category add(@NotNull @Valid Category category);

    Category getById(long id);

    List<Category> getAllInWindow(@Positive int windowSize, @PositiveOrZero int windowIndex);

    Category update(long id, @NotNull @Valid CategoryPatch patch);

    void removeById(long id);
}
