package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.entity.User;

import java.util.List;

@Component
public class UserMapper {
    public User mapToUser(NewUserRequest dto) {
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

    public UserDto mapToDto(final User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> mapToDto(final List<User> users) {
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
