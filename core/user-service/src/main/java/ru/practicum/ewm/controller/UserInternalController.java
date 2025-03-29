package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.HttpRequestResponseLogger;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController extends HttpRequestResponseLogger {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable final long id, final HttpServletRequest request) {
        logHttpRequest(request);
        boolean isExists = userService.existsById(id);
        logHttpResponse(request, isExists);
        return isExists;
    }

    @GetMapping
    public List<UserShortDto> get(@RequestParam final List<Long> ids, final HttpServletRequest request) {
        logHttpRequest(request);
        List<UserShortDto> response = userMapper.mapToShortDto(userService.findByIds(ids, Pageable.unpaged()));
        logHttpResponse(request, response);
        return response;
    }
}
