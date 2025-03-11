package ru.practicum.ewm.user;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    User mapToUser(NewUserRequest dto) {
        if (dto == null) {
            return null;
        }
        final User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        return user;
    }

    public User mapToUser(final Long id) {
        if (id == null) {
            return null;
        }
        final User user = new User();
        user.setId(id);
        return user;
    }

    UserDto mapToDto(final User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    List<UserDto> mapToDto(final List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserShortDto mapToShortDto(final User user) {
        if (user == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
