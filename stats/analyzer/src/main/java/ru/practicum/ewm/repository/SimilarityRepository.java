package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Similarity;

import java.util.List;
import java.util.Optional;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {
    Optional<Similarity> findByEventAIdAndEventBId(long eventAId, long eventBId);

    @Query("""
            SELECT s
            FROM Similarity s
            WHERE s.eventAId = :sampleEventId OR s.eventBId = :sampleEventId
            ORDER BY s.score DESC
            """)
    List<Similarity> findSimilarTo(@Param("sampleEventId") long sampleEventId);

    @Query("""
        SELECT
             CASE
               WHEN s.eventAId IN :eventIds THEN s.eventBId
               ELSE s.eventAId
             END
         FROM Similarity s
         WHERE s.eventAId IN :eventIds OR s.eventBId IN :eventIds
""")
    List<Long> findAllSimilarEventIds(List<Long> eventIds);

    @Query("""
            SELECT s
            FROM Similarity s
            WHERE
                (s.eventAId IN :newEventIds AND s.eventBId IN :interactedEventIds)
                OR
                (s.eventBId IN :newEventIds AND s.eventAId IN :interactedEventIds)
            """)
    List<Similarity> findSimilaritiesBetweenNewAndInteracted(
            @Param("newEventIds") List<Long> newEventIds,
            @Param("interactedEventIds") List<Long> interactedEventIds);

}
