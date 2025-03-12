package ru.practicum.ewm.event;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Location {

    private Float lat;
    private Float lon;
}
