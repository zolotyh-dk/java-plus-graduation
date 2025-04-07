package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.RecommendedEventProjection;
import ru.practicum.ewm.model.Weight;

import java.util.List;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {
    Optional<Weight> findByEventIdAndUserId(long eventId, long userId);

    @Query(value = """
    SELECT event_id AS eventId, SUM(weight) AS score
    FROM weights
    WHERE event_id IN :eventIds
    GROUP BY event_id
    """, nativeQuery = true)
    List<RecommendedEventProjection> findTotalWeightByEventIds(@Param("eventIds") List<Long> eventIds);

}
