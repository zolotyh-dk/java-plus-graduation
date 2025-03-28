package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.model.Request;

import java.util.List;

@Component
public class RequestMapper {
    public RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .requester(request.getRequesterId())
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEventId())
                .status(request.getStatus())
                .build();
    }

    public List<RequestDto> mapToRequestDto(final List<Request> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(this::mapToRequestDto)
                .toList();
    }
}
