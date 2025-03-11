package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.Size;
import ru.practicum.ewm.validation.NotBlankOrNull;

import java.util.Set;

public record UpdateCompilationRequest(
        Set<Long> events,

        Boolean pinned,

        @NotBlankOrNull
        @Size(min = 1, max = 50)
        String title
) {
}
