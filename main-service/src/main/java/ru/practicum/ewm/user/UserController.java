package ru.practicum.ewm.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.HttpRequestResponseLogger;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController extends HttpRequestResponseLogger {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> get(@RequestParam(required = false) final List<Long> ids,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                   @RequestParam(defaultValue = "10") @Positive final int size,
                                   final HttpServletRequest request) {
        logHttpRequest(request);
        final PageRequest pageRequest = PageRequest.of(from / size, size);
        Collection<UserDto> response;
        if (CollectionUtils.isEmpty(ids)) {
            response = userService.findAll(pageRequest);
        } else {
            response = userService.findByIds(ids, pageRequest);
        }
        logHttpResponse(request, response);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@RequestBody @Valid final NewUserRequest requestDto,
                        final HttpServletRequest request) {
        logHttpRequest(request, requestDto);
        final UserDto responseDto = userService.save(requestDto);
        logHttpResponse(request, responseDto);
        return responseDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final long id, final HttpServletRequest request) {
        logHttpRequest(request);
        userService.delete(id);
        logHttpResponse(request);
    }
}
