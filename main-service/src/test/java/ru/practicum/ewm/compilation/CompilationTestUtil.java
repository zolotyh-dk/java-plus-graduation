package ru.practicum.ewm.compilation;

import java.util.Set;

import static ru.practicum.ewm.event.EventTestUtil.*;

public class CompilationTestUtil {
    public static final long COMPILATION_ID_1 = 1L;
    public static final long COMPILATION_ID_2 = 2L;
    public static final String COMPILATION_TITLE_1 = "Compilation title 1";
    public static final String COMPILATION_TITLE_2 = "Compilation title 2";

    public static final CompilationDto COMPILATION_DTO_1 = CompilationDto.builder()
            .id(COMPILATION_ID_1)
            .events(Set.of(EVENT_SHORT_DTO_1))
            .pinned(true)
            .title(COMPILATION_TITLE_1)
            .build();

    public static final NewCompilationDto NEW_COMPILATION_DTO = new NewCompilationDto(
            Set.of(EVENT_ID_1),
            true,
            COMPILATION_TITLE_2
    );

    public static final NewCompilationDto NEW_COMPILATION_DTO_WITH_NULL_TITLE = new NewCompilationDto(
            Set.of(EVENT_ID_1, EVENT_ID_2),
            true,
            null
    );

    public static final NewCompilationDto NEW_COMPILATION_DTO_WITH_BLANK_TITLE = new NewCompilationDto(
            Set.of(EVENT_ID_1, EVENT_ID_2),
            true,
            "   "
    );

    public static final NewCompilationDto NEW_COMPILATION_DTO_WITH_LONG_TITLE = new NewCompilationDto(
            Set.of(EVENT_ID_1, EVENT_ID_2),
            true,
            "A".repeat(51)
    );

    public static final UpdateCompilationRequest UPDATE_COMPILATION_REQUEST = new UpdateCompilationRequest(
            Set.of(EVENT_ID_1),
            true,
            COMPILATION_TITLE_1
    );

    public static final UpdateCompilationRequest UPDATE_COMPILATION_REQUEST_WITH_BLANK_TITLE = new UpdateCompilationRequest(
            Set.of(EVENT_ID_1, EVENT_ID_2),
            true,
            "   "
    );

    public static final UpdateCompilationRequest UPDATE_COMPILATION_REQUEST_WITH_LONG_TITLE = new UpdateCompilationRequest(
            Set.of(EVENT_ID_1, EVENT_ID_2),
            true,
            "A".repeat(51)
    );
}
