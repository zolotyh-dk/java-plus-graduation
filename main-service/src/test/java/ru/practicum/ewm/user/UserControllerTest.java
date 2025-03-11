package ru.practicum.ewm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.user.UserTestUtil.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private NewUserRequest newUserRequest;
    private List<UserDto> userDtos;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(USER_ID_1);
        user1.setName(USER_NAME_1);
        user1.setEmail(EMAIL_1);

        user2 = new User();
        user2.setId(USER_ID_2);
        user2.setName(USER_NAME_2);
        user2.setEmail(EMAIL_2);

        userDto1 = UserDto.builder().id(USER_ID_1).name(USER_NAME_1).email(EMAIL_1).build();
        userDto2 = UserDto.builder().id(USER_ID_2).name(USER_NAME_2).email(EMAIL_2).build();
        newUserRequest = new NewUserRequest(EMAIL_1, USER_NAME_1);

        userDtos = List.of(userDto1, userDto2);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testGet() throws Exception {
        when(userService.findAll(PAGEABLE)).thenReturn(userDtos);
        mvc.perform(get("/admin/users")
                .param("from", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is(EMAIL_1)))
                .andExpect(jsonPath("$[0].name", is(USER_NAME_1)))
                .andExpect(jsonPath("$[1].email", is(EMAIL_2)))
                .andExpect(jsonPath("$[1].name", is(USER_NAME_2)));
        verify(userService).findAll(PAGEABLE);
    }

    @Test
    void testGetWithNegativeFrom() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllWithZeroSize() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindByIds() throws Exception {
        List<Long> ids = List.of(USER_ID_1, USER_ID_2);
        when(userService.findByIds(ids, PAGEABLE)).thenReturn(userDtos);
        mvc.perform(get("/admin/users")
                        .param("from", "0")
                        .param("size", "10")
                        .param("ids", ids.stream().map(String::valueOf).toArray(String[]::new)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is(EMAIL_1)))
                .andExpect(jsonPath("$[0].name", is(USER_NAME_1)))
                .andExpect(jsonPath("$[1].email", is(EMAIL_2)))
                .andExpect(jsonPath("$[1].name", is(USER_NAME_2)));
        verify(userService).findByIds(ids, PAGEABLE);
    }

    @Test
    void testSave() throws Exception {
        when(userService.save(newUserRequest)).thenReturn(userDto1);
        mvc.perform(post("/admin/users")
                .content(mapper.writeValueAsString(newUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(USER_NAME_1)))
                .andExpect(jsonPath("$.email", is(EMAIL_1)));
        verify(userService).save(newUserRequest);
    }

    @Test
    void testSaveUserWithInvalidName() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest(EMAIL_1, "   ");

        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveUserWithInvalidEmail() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest("invalid-email", USER_NAME_1);

        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(userService).delete(USER_ID_1);
        mvc.perform(delete("/admin/users/{id}", USER_ID_1))
                .andExpect(status().isNoContent());
        verify(userService).delete(USER_ID_1);
    }

    @Test
    void testDeleteWhenNotFound() throws Exception {
        doThrow(new NotFoundException(User.class.getName(), USER_ID_1)).when(userService).delete(USER_ID_1);
        mvc.perform(delete("/admin/users/{id}", USER_ID_1))
                .andExpect(status().isNotFound());
        verify(userService).delete(USER_ID_1);
    }
}
