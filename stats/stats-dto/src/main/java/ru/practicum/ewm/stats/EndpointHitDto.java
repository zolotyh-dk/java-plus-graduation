package ru.practicum.ewm.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record EndpointHitDto(

        @NotBlank
        @Size(max = 255)
        String app,

        @NotBlank
        @Size(max = 512)
        String uri,

        @NotBlank
        @Size(max = 40)
        String ip,

        @NotNull
        @PastOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp) {

}
