package ru.practicum.ewm.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/admin/users/exists/{id}")
    boolean existsById(@PathVariable long id);

    @GetMapping("/admin/users")
    List<UserShortDto> findAllByIdIn(@RequestParam List<Long> ids);
}