package ru.practicum.ewm.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record CategoryCreateDto(

        @NotBlank
        @Size(max = 50)
        String name) {

}
