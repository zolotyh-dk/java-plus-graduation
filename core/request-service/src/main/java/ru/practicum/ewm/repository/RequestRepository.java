package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.request.dto.RequestState;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdAndEventIdAndStatusNotLike(long userId, long eventId, RequestState status);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);

    List<Request> findAllByEventIdAndEventInitiatorIdAndIdIn(long eventId, long initiatorId, List<Long> ids);

    List<Request> findAllByEventIdAndEventInitiatorIdAndStatus(long eventId, long initiatorId, RequestState status);
}
