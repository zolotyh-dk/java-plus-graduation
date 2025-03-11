package ru.practicum.ewm.stats;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.BaseExceptionHandler;

import java.time.Clock;

@RestControllerAdvice
class ControllerExceptionHandler extends BaseExceptionHandler {

    ControllerExceptionHandler(final Clock clock) {
        super(clock);
    }
}
