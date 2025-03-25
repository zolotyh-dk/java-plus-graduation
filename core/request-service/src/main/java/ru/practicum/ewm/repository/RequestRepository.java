package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.request.dto.RequestState;
import ru.practicum.ewm.service.EventRequestStats;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdAndEventIdAndStatusNotLike(long userId, long eventId, RequestState status);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);

    List<Request> findAllByEventIdAndEventInitiatorIdAndIdIn(long eventId, long initiatorId, List<Long> ids);

    List<Request> findAllByEventIdAndEventInitiatorIdAndStatus(long eventId, long initiatorId, RequestState status);

    @Query("select r.eventId as id, count(r.id) as requests " +
            "from Request r " +
            "where r.eventId in :ids and r.status = 'CONFIRMED' " +
            "group by r.eventId")
    List<EventRequestStats> getConfirmedRequestStats(@Param("ids") List<Long> ids);
}
