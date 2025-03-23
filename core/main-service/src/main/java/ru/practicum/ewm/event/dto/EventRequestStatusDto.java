package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public record EventRequestStatusDto(

        List<RequestDto> confirmedRequests,
        List<RequestDto> rejectedRequests
) {

}
