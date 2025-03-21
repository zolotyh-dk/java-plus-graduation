package ru.practicum.ewm.subscription;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "subscriptions")
@Data
@EqualsAndHashCode(of = "id")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;
}
