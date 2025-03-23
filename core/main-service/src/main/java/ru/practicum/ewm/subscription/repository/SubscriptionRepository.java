package ru.practicum.ewm.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.subscription.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsBySubscriberIdAndTargetId(long subscriberId, long targetId);

    Optional<Subscription> findBySubscriberIdAndTargetId(long subscriberId, long targetId);

    @Query("SELECT s.targetId FROM Subscription s WHERE s.subscriberId = :subscriberId")
    List<Long> findTargetIdsBySubscriberId(@Param("subscriberId") Long subscriberId);
}
