package ru.practicum.shareit.abstraction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Entity {
    private Long id;
    @JsonIgnore
    private Long userIdHeader;
}
