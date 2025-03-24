package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventRequestStats;
import ru.practicum.ewm.request.dto.RequestState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(long id, long userId);

    Optional<Event> findByIdAndState(long id, EventState state);

    Set<Event> findAllByIdIn(Set<Long> ids);

    @Query("select e.id as id, count(r.id) as requests from Event e join Request r on e = r.event "
            + "where e.id in :ids and r.status = :status group by e.id")
    List<EventRequestStats> getRequestStats(@Param("ids") List<Long> ids, @Param("status") final RequestState status);

    boolean existsByIdAndInitiatorId(long userId, long eventId);
}
