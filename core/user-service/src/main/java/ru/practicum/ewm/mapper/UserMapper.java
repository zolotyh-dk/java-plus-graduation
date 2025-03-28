package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.model.User;

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

    public UserShortDto mapToShortDto(final UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(userDto.id())
                .name(userDto.name())
                .build();
    }

    public List<UserShortDto> mapToShortDto(List<UserDto> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::mapToShortDto)
                .toList();
    }
}
