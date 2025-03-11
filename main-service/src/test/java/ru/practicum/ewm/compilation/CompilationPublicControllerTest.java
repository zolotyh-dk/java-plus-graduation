package ru.practicum.ewm.compilation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.category.CategoryTestUtil.CATEGORY_NAME_1;
import static ru.practicum.ewm.compilation.CompilationTestUtil.*;
import static ru.practicum.ewm.event.EventTestUtil.*;
import static ru.practicum.ewm.user.UserTestUtil.PAGEABLE;
import static ru.practicum.ewm.user.UserTestUtil.USER_NAME_1;

@WebMvcTest(controllers = CompilationPublicController.class)
public class CompilationPublicControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CompilationService compilationService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(compilationService);
    }

    @Test
    void testGetWithPinnedIsNull() throws Exception {
        when(compilationService.getAll(null, PAGEABLE))
                .thenReturn(List.of(COMPILATION_DTO_1));
        mvc.perform(get("/compilations")
                .param("from", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].events", hasSize(1)))
                .andExpect(jsonPath("$[0].events[*].initiator.name", hasItem(USER_NAME_1)))
                .andExpect(jsonPath("$[0].events[*].title", hasItem(EVENT_TITLE_1)))
                .andExpect(jsonPath("$[0].events[*].category.name", hasItem(CATEGORY_NAME_1)))
                .andExpect(jsonPath("$[0].events[*].eventDate", hasItem(EVENT_DATE_1.format(formatter))))
                .andExpect(jsonPath("$[0].events[*].annotation", hasItem(EVENT_ANNOTATION_1)))
                .andExpect(jsonPath("$[0].events[*].paid", hasItem(false)))
                .andExpect(jsonPath("$[0].events[*].confirmedRequests", hasItem(1)))
                .andExpect(jsonPath("$[0].events[*].views", hasItem(1)))
                .andExpect(jsonPath("$[0].pinned", is(true)))
                .andExpect(jsonPath("$[0].title", is(COMPILATION_TITLE_1)));
        verify(compilationService).getAll(null, PAGEABLE);
    }

    @Test
    void testGetWithNegativeFrom() throws Exception {
        mvc.perform(get("/compilations")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetWithZeroSize() throws Exception {
        mvc.perform(get("/compilations")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetById() throws Exception {
        when(compilationService.getById(COMPILATION_ID_1))
                .thenReturn(COMPILATION_DTO_1);
        mvc.perform(get("/compilations/{id}", COMPILATION_ID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events", hasSize(1)))
                .andExpect(jsonPath("$.events[*].initiator.name", hasItem(USER_NAME_1)))
                .andExpect(jsonPath("$.events[*].title", hasItem(EVENT_TITLE_1)))
                .andExpect(jsonPath("$.events[*].category.name", hasItem(CATEGORY_NAME_1)))
                .andExpect(jsonPath("$.events[*].eventDate", hasItem(
                        EVENT_DATE_1.format(formatter))))
                .andExpect(jsonPath("$.events[*].annotation", hasItem(EVENT_ANNOTATION_1)))
                .andExpect(jsonPath("$.events[*].paid", hasItem(false)))
                .andExpect(jsonPath("$.events[*].confirmedRequests", hasItem(1)))
                .andExpect(jsonPath("$.events[*].views", hasItem(1)))
                .andExpect(jsonPath("$.pinned", is(true)))
                .andExpect(jsonPath("$.title", is(COMPILATION_TITLE_1)));
        verify(compilationService).getById(COMPILATION_ID_1);
    }

    @Test
    void testGetByIdWhenNotFound() throws Exception {
        when(compilationService.getById(COMPILATION_ID_1))
                .thenThrow(new NotFoundException(Compilation.class, COMPILATION_ID_1));
        mvc.perform(get("/compilations/{id}", COMPILATION_ID_1))
                .andExpect(status().isNotFound());
        verify(compilationService).getById(COMPILATION_ID_1);
    }
}
