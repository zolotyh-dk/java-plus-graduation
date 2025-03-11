package ru.practicum.ewm.stats;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Data
@EqualsAndHashCode(of = "id")
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String app;

    @NotBlank
    @Size(max = 512)
    private String uri;

    @NotBlank
    @Size(max = 40)
    private String ip;

    @NotNull
    @PastOrPresent
    private LocalDateTime timestamp;
}
