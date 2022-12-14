package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.abstraction.EntityDto;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto extends EntityDto {
    @JsonProperty("id")
    private Long commentId;
    @JsonProperty("text")
    private String commentText;
    @JsonProperty("created")
    private LocalDateTime commentDate;
    private String authorName;
    private Long itemId;
    private Long userIdHeader;
}
