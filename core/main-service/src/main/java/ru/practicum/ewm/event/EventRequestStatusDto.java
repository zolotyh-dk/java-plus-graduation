package ru.practicum.ewm.event;

import ru.practicum.ewm.request.RequestDto;

import java.util.List;

public record EventRequestStatusDto(

        List<RequestDto> confirmedRequests,
        List<RequestDto> rejectedRequests
) {

}
