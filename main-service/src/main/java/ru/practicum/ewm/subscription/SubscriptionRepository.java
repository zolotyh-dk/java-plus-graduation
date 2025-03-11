package ru.practicum.ewm.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsBySubscriberIdAndTargetId(long subscriberId, long targetId);

    Optional<Subscription> findBySubscriberIdAndTargetId(long subscriberId, long targetId);

    @Query("SELECT s.target.id FROM Subscription s WHERE s.subscriber.id = :subscriberId")
    List<Long> findTargetIdsBySubscriberId(@Param("subscriberId") Long subscriberId);
}
