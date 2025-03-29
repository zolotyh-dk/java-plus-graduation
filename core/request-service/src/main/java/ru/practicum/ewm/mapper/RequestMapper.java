package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", source = "eventId")
    RequestDto mapToRequestDto(Request request);

    List<RequestDto> mapToRequestDto(List<Request> requests);
}
