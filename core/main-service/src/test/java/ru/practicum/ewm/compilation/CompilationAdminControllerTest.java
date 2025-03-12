package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.category.CategoryTestUtil.CATEGORY_NAME_1;
import static ru.practicum.ewm.compilation.CompilationTestUtil.*;
import static ru.practicum.ewm.event.EventTestUtil.*;
import static ru.practicum.ewm.user.UserTestUtil.USER_NAME_1;

@WebMvcTest(controllers = CompilationAdminController.class)
public class CompilationAdminControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CompilationService compilationService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(compilationService);
    }

    @Test
    void testSaveNewCompilation() throws Exception {
        when(compilationService.save(NEW_COMPILATION_DTO)).thenReturn(COMPILATION_DTO_1);
        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(NEW_COMPILATION_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.events", hasSize(1)))
                .andExpect(jsonPath("$.events[0].initiator.name", is(USER_NAME_1)))
                .andExpect(jsonPath("$.events[0].title", is(EVENT_TITLE_1)))
                .andExpect(jsonPath("$.events[0].category.name", is(CATEGORY_NAME_1)))
                .andExpect(jsonPath("$.events[0].eventDate", is(EVENT_DATE_1.format(formatter))))
                .andExpect(jsonPath("$.events[0].annotation", is(EVENT_ANNOTATION_1)))
                .andExpect(jsonPath("$.events[0].paid", is(false)))
                .andExpect(jsonPath("$.events[0].confirmedRequests", is(1)))
                .andExpect(jsonPath("$.events[0].views", is(1)))
                .andExpect(jsonPath("$.pinned", is(true)))
                .andExpect(jsonPath("$.title", is(COMPILATION_TITLE_1)));
        verify(compilationService).save(NEW_COMPILATION_DTO);
    }

    @Test
    void testSaveNewCompilationWithNullTitle() throws Exception {
        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(NEW_COMPILATION_DTO_WITH_NULL_TITLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveNewCompilationWithBlankTitle() throws Exception {
        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(NEW_COMPILATION_DTO_WITH_BLANK_TITLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveNewCompilationWithLongTitle() throws Exception {
        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(NEW_COMPILATION_DTO_WITH_LONG_TITLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(compilationService).delete(COMPILATION_ID_1);
        mvc.perform(delete("/admin/compilations/{id}", COMPILATION_ID_1))
                .andExpect(status().isNoContent());
        verify(compilationService).delete(COMPILATION_ID_1);
    }

    @Test
    void testDeleteWhenNotFound() throws Exception {
        doThrow(new NotFoundException(Compilation.class, COMPILATION_ID_1)).when(compilationService).delete(COMPILATION_ID_1);
        mvc.perform(delete("/admin/compilations/{id}", COMPILATION_ID_1))
                .andExpect(status().isNotFound());
        verify(compilationService).delete(COMPILATION_ID_1);
    }

    @Test
    void testUpdate() throws Exception {
        when(compilationService.update(COMPILATION_ID_1, UPDATE_COMPILATION_REQUEST)).thenReturn(COMPILATION_DTO_1);
        mvc.perform(patch("/admin/compilations/{id}", COMPILATION_ID_1)
                        .content(mapper.writeValueAsString(UPDATE_COMPILATION_REQUEST))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events", hasSize(1)))
                .andExpect(jsonPath("$.events[*].initiator.name", hasItem(USER_NAME_1)))
                .andExpect(jsonPath("$.events[*].title", hasItem(EVENT_TITLE_1)))
                .andExpect(jsonPath("$.events[*].category.name", hasItem(CATEGORY_NAME_1)))
                .andExpect(jsonPath("$.events[*].eventDate", hasItem(EVENT_DATE_1.format(formatter))))
                .andExpect(jsonPath("$.events[*].annotation", hasItem(EVENT_ANNOTATION_1)))
                .andExpect(jsonPath("$.events[*].paid", hasItem(false)))
                .andExpect(jsonPath("$.events[*].confirmedRequests", hasItem(1)))
                .andExpect(jsonPath("$.events[*].views", hasItem(1)))
                .andExpect(jsonPath("$.pinned", is(true)))
                .andExpect(jsonPath("$.title", is(COMPILATION_TITLE_1)));
        verify(compilationService).update(COMPILATION_ID_1, UPDATE_COMPILATION_REQUEST);
    }

    @Test
    void testUpdateWithBlankTitle() throws Exception {
        mvc.perform(patch("/admin/compilations/{id}", COMPILATION_ID_1)
                        .content(mapper.writeValueAsString(UPDATE_COMPILATION_REQUEST_WITH_BLANK_TITLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWithLongTitle() throws Exception {
        mvc.perform(patch("/admin/compilations/{id}", COMPILATION_ID_1)
                        .content(mapper.writeValueAsString(UPDATE_COMPILATION_REQUEST_WITH_LONG_TITLE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
