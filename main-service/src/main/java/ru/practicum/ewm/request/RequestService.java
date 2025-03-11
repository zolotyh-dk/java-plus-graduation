package ru.practicum.ewm.request;

import java.util.List;

public interface RequestService {

    RequestDto create(long userId, long eventId);

    List<RequestDto> getAllRequestByUserId(long userId);

    RequestDto cancel(long userId, long requestId);
}
