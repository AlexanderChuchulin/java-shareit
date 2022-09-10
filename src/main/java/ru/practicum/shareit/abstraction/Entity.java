package ru.practicum.shareit.abstraction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class Entity {
    private Integer id;
    @JsonIgnore
    private Integer userIdHeader;
}
