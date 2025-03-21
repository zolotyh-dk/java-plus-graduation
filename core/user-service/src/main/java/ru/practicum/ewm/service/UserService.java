package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;

import java.util.List;

public interface UserService {

    User getById(long id);

    List<UserDto> findAll(Pageable pageable);

    List<UserDto> findByIds(List<Long> ids, Pageable pageable);

    UserDto save(NewUserRequest requestDto);

    void delete(long id);
}
