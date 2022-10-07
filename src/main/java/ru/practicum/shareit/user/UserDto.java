package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.abstraction.EntityDto;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends EntityDto {
    @JsonProperty("id")
    private Long userId;
    private String email;
    @JsonProperty("name")
    private String userName;
}
