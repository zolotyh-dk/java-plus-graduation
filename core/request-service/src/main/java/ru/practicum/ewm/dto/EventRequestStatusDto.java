package ru.practicum.ewm.dto;

import java.util.List;

/**
 * Статус заявок изменён
 *
 * @param confirmedRequests Список подтвержденных заявок
 * @param rejectedRequests Список отклоненных заявок
 */

public record EventRequestStatusDto(

        List<RequestDto> confirmedRequests,
        List<RequestDto> rejectedRequests
) {

}
