package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Weight;

import java.util.List;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {
    Optional<Weight> findByEventIdAndUserId(long eventId, long userId);

    List<Weight> findAllByEventIdIn(List<Long> eventIds);
}
