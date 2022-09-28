package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.abstraction.EntityDto;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto extends EntityDto {
    @JsonProperty("id")
    private Long itemId;
    @JsonProperty("name")
    private String itemName;
    @JsonProperty("description")
    private String itemDescription;
    @JsonProperty("available")
    private Boolean isItemAvailable;
    private User owner;
    private ItemRequest itemRequest;
    @JsonInclude
    private BookingDto nextBooking;
    @JsonInclude
    private BookingDto lastBooking;
    @JsonProperty("comments")
    private List<CommentDto> commentsDtoList;
    private Long userIdHeader;
}
