package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.abstraction.EntityDto;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto extends EntityDto {
    @JsonProperty("id")
    private Long userId;
    private String email;
    @JsonProperty("name")
    private String userName;
}
