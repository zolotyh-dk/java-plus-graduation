package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestService {

    Request create(long userId, long eventId);

    List<Request> getAllRequestByUserId(long userId);

    Request cancel(long userId, long requestId);
}
