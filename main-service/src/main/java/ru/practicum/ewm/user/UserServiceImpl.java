package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public User getById(final long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
    }

    @Override
    public List<UserDto> findAll(final Pageable pageable) {
        final List<User> users = userRepository.findAll(pageable).getContent();
        return mapper.mapToDto(users);
    }

    @Override
    public List<UserDto> findByIds(final List<Long> ids, final Pageable pageable) {
        final List<User> users = userRepository.findByIdIn(ids, pageable).getContent();
        return mapper.mapToDto(users);
    }

    @Transactional
    @Override
    public UserDto save(final NewUserRequest requestDto) {
        final User user = mapper.mapToUser(requestDto);
        return mapper.mapToDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void delete(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(User.class, id);
        }
        userRepository.deleteById(id);
    }
}
