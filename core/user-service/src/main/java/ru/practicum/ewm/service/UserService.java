package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserService {

    User getById(long id);

    List<UserDto> findAll(Pageable pageable);

    List<UserDto> findByIds(List<Long> ids, Pageable pageable);

    UserDto save(NewUserRequest requestDto);

    void delete(long id);

    boolean existsById(long id);
}
