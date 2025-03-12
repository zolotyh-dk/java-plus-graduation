package ru.practicum.ewm.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.user.UserTestUtil.*;

@Transactional
@SpringBootTest
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void testFindAll() {
        List<UserDto> result = userService.findAll(PAGEABLE);
        assertThat(result.stream().map(UserDto::name).toList(),
                containsInAnyOrder(USER_NAME_1, USER_NAME_2));
        assertThat(result.stream().map(UserDto::email).toList(),
                containsInAnyOrder(EMAIL_1, EMAIL_2));
    }

    @Test
    void testFindByIds() {
        List<Long> ids = List.of(USER_ID_1, USER_ID_2);
        List<UserDto> result = userService.findByIds(ids, PAGEABLE);
        assertThat(result.stream().map(UserDto::name).toList(),
                containsInAnyOrder(USER_NAME_1, USER_NAME_2));
        assertThat(result.stream().map(UserDto::email).toList(),
                containsInAnyOrder(EMAIL_1, EMAIL_2));
    }

    @Test
    void testFindByIdsWhenNotExists() {
        List<Long> ids = List.of(USER_ID_3);
        List<UserDto> result = userService.findByIds(ids, PAGEABLE);
        assertThat(result, empty());
    }

    @Test
    void testSaveNewUser() {
        NewUserRequest request = new NewUserRequest(EMAIL_3, USER_NAME_3);

        UserDto result = userService.save(request);
        assertThat(result.name(), is((USER_NAME_3)));
        assertThat(result.email(), is(EMAIL_3));

        Optional<User> user = userRepository.findById(USER_ID_3);
        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getName(), is(USER_NAME_3));
        assertThat(user.get().getEmail(), is(EMAIL_3));
    }

    @Test
    void testDelete() {
        assertThat(userRepository.existsById(USER_ID_2), is(true));
        userService.delete(USER_ID_2);
        assertThat(userRepository.existsById(USER_ID_2), is(false));
    }

    @Test
    void testDeleteWhenNotExists() {
        assertThat(userRepository.existsById(USER_ID_3), is(false));
        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.delete(USER_ID_3));
        assertThat(e.getMessage(), is("User with id = " + USER_ID_3 + " not found"));
    }
}
