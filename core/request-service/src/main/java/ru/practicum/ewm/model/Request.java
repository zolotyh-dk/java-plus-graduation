package ru.practicum.ewm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.request.dto.RequestState;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@EqualsAndHashCode(of = "id")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long eventId;

    @CreationTimestamp
    private LocalDateTime created;

    @NotNull
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    @NotNull
    RequestState status;
}
