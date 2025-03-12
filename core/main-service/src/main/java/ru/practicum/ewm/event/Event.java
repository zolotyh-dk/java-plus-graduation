package ru.practicum.ewm.event;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User initiator;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Category category;

    @NotNull
    private LocalDateTime eventDate;

    @Embedded
    @NotNull
    private Location location;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @PositiveOrZero
    private long participantLimit;

    private boolean paid;
    private boolean requestModeration;

    @Transient
    private long confirmedRequests;

    @Transient
    private long views;

    @NotNull
    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EventState state;
}
