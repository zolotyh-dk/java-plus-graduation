package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.Size;
import ru.practicum.ewm.validation.NotBlankOrNull;

public record CategoryUpdateDto(
        @NotBlankOrNull
        @Size(max = 50)
        String name) {
}
