package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.ValidationGroup.CreateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemDto {
    @NotBlank(groups = CreateGroup.class)
    @Size(min = 1, max = 255)
    private String name;
    @NotBlank(groups = CreateGroup.class)
    @Size(min = 1, max = 5000)
    private String description;
    @NotNull(groups = CreateGroup.class)
    private Boolean available;
    private Long requestId;
}
