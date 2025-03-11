package ru.practicum.ewm.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.ewm.user.UserTestUtil.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private NewUserRequest newUserRequest;
    private List<User> users;
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

        users = List.of(user1, user2);
        userDtos = List.of(userDto1, userDto2);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, mapper);
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll(PAGEABLE)).thenReturn(new PageImpl<>(users));
        when(mapper.mapToDto(users)).thenReturn(userDtos);

        List<UserDto> result = userService.findAll(PAGEABLE);

        assertThat(result, is(userDtos));

        verify(userRepository).findAll(PAGEABLE);
        verify(mapper).mapToDto(users);
    }

    @Test
    void testFindByIds() {
        List<Long> ids = List.of(USER_ID_1, USER_ID_2);

        when(userRepository.findByIdIn(ids, PAGEABLE)).thenReturn(new PageImpl<>(users));
        when(mapper.mapToDto(users)).thenReturn(userDtos);

        List<UserDto> result = userService.findByIds(ids, PAGEABLE);

        assertThat(result, is(userDtos));

        verify(userRepository).findByIdIn(ids, PAGEABLE);
        verify(mapper).mapToDto(users);
    }

    @Test
    void testSave() {
        when(mapper.mapToUser(newUserRequest)).thenReturn(user1);
        when(userRepository.save(user1)).thenReturn(user1);
        when(mapper.mapToDto(user1)).thenReturn(userDto1);

        UserDto result = userService.save(newUserRequest);

        assertThat(result, is(userDto1));

        verify(mapper).mapToUser(newUserRequest);
        verify(userRepository).save(user1);
        verify(mapper).mapToDto(user1);
    }

    @Test
    void delete() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        userService.delete(USER_ID_1);

        verify(userRepository).deleteById(USER_ID_1);
    }

    @Test
    void testDeleteWhetNotExists() {
        when(userRepository.existsById(USER_ID_1)).thenReturn(false);

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.delete(USER_ID_1));
        assertThat(e.getMessage(), is("User with id = " + USER_ID_1 + " not found"));

        verify(userRepository).existsById(USER_ID_1);
        verify(userRepository, never()).deleteById(anyLong());
    }
}