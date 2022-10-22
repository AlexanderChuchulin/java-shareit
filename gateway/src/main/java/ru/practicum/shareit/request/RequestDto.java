package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class RequestDto {
    @NotBlank
    @Size(min = 1, max = 5000)
    private String description;
}
