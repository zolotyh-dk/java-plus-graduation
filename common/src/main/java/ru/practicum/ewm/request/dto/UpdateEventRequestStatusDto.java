package ru.practicum.ewm.request.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Новый статус для заявок на участие в событии текущего пользователя
 *
 * @param requestIds Идентификаторы запросов на участие в событии текущего пользователя
 * @param status Новый статус запроса на участие в событии текущего пользователя ("CONFIRMED" "REJECTED")
 */
public record UpdateEventRequestStatusDto(
        List<Long> requestIds,

        @NotNull
        RequestState status
) {
}
