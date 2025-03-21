package ru.practicum.ewm.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.event.Event;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@EqualsAndHashCode(of = "id")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    Event event;

    @CreationTimestamp
    private LocalDateTime created;

    @NotNull
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    @NotNull
    RequestState status;
}
