package ru.practicum.ewm.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserRequest(
        @NotBlank
        @Email
        @Size(min = 6, max = 254)
        String email,

        @NotBlank
        @Size(min = 2, max = 250)
        String name) {
}
