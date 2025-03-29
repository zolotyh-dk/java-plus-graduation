package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User mapToUser(NewUserRequest dto);

    User mapToUser(Long id);

    UserDto mapToDto(User user);

    List<UserDto> mapToDto(List<User> users);

    UserShortDto mapToShortDto(UserDto userDto);

    List<UserShortDto> mapToShortDto(List<UserDto> users);
}
