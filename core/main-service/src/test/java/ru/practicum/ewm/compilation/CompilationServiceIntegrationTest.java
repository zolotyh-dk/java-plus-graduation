package ru.practicum.ewm.compilation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.compilation.CompilationTestUtil.*;
import static ru.practicum.ewm.event.EventTestUtil.EVENT_ID_1;
import static ru.practicum.ewm.user.UserTestUtil.PAGEABLE;

@Transactional
@SpringBootTest
public class CompilationServiceIntegrationTest {
    @Autowired
    private CompilationRepository compilationRepository;

    @Autowired
    private CompilationService compilationService;

    @Test
    void testGetAll() {
        List<CompilationDto> result = compilationService.getAll(null, PAGEABLE);
        assertThat(result.get(0).title(), is(COMPILATION_TITLE_1));
        assertThat(result.get(0).pinned(), is(true));
        assertThat(result.get(0).events(), hasSize(1));
        assertThat(result.get(0).events().stream().findFirst().get().title(), is("Concert"));
    }

    @Test
    void testGetOnlyPinned() {
        List<CompilationDto> result = compilationService.getAll(true, PAGEABLE);
        assertThat(result.get(0).title(), is(COMPILATION_TITLE_1));
        assertThat(result.get(0).pinned(), is(true));
        assertThat(result.get(0).events(), hasSize(1));
        assertThat(result.get(0).events().stream().findFirst().get().title(), is("Concert"));
    }

    @Test
    void testGetOnlyNotPinned_ShouldReturnEmptyList() {
        List<CompilationDto> result = compilationService.getAll(false, PAGEABLE);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void testGetById() {
        CompilationDto result = compilationService.getById(COMPILATION_ID_1);
        assertThat(result.title(), is(COMPILATION_TITLE_1));
        assertThat(result.pinned(), is(true));
        assertThat(result.events(), hasSize(1));
        assertThat(result.events().stream().findFirst().get().title(), is("Concert"));
    }

    @Test
    void testGetByIdWhenNotExists_ShouldThrowException() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> compilationService.getById(COMPILATION_ID_2));
        assertThat(e.getMessage(), is("Compilation with id = " + COMPILATION_ID_2 + " not found"));
    }

    @Test
    void testSaveNewCompilation() {
        CompilationDto result = compilationService.save(NEW_COMPILATION_DTO);
        assertThat(result.title(), is((COMPILATION_TITLE_2)));
        assertThat(result.pinned(), is(true));
        assertThat(result.events(), hasSize(1));

        Optional<Compilation> compilation = compilationRepository.findById(result.id());
        assertThat(compilation.isPresent(), is(true));
        assertThat(compilation.get().getTitle(), is((COMPILATION_TITLE_2)));
        assertThat(compilation.get().getPinned(), is(true));
        assertThat(compilation.get().getEvents(), hasSize(1));
    }

    @Test
    void testUpdateCompilation() {
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest(null, false, "Updated Title");
        CompilationDto result = compilationService.update(COMPILATION_ID_1, updateRequest);

        assertThat(result.title(), is("Updated Title"));
        assertThat(result.pinned(), is(false));
        assertThat(result.events(), hasSize(1));
        assertThat(result.events().stream().findFirst().get().id(), is(EVENT_ID_1));
    }

}
