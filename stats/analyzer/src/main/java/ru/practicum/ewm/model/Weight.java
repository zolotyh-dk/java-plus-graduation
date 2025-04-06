package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * Максимальный вес взаимодействия пользователя с мероприятием
 */
@Entity
@Table(name = "weight")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Weight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "event_id", nullable = false)
    private long eventId;

    @Column(name = "score", nullable = false)
    private double weight;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
