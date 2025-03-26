package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(long id, long userId);

    Optional<Event> findByIdAndState(long id, EventState state);

    Set<Event> findAllByIdIn(Set<Long> ids);

    boolean existsByIdAndInitiatorId(long userId, long eventId);
}
