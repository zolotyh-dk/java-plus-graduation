package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdAndEventIdAndStatusNotLike(long userId, long eventId, RequestState status);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);

    List<Request> findAllByEventIdAndEventInitiatorIdAndIdIn(long eventId, long initiatorId, List<Long> ids);

    List<Request> findAllByEventIdAndEventInitiatorIdAndStatus(long eventId, long initiatorId, RequestState status);
}
