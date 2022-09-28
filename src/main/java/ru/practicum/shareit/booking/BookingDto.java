package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.abstraction.EntityDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto extends EntityDto {
    @JsonProperty("id")
    private Long bookingId;
    @JsonProperty("start")
    private LocalDateTime bookingStart;
    @JsonProperty("end")
    private LocalDateTime bookingEnd;
    @JsonProperty("status")
    private BookingStatus bookingStatus;
    @JsonIgnoreProperties({"nextBookingDto", "lastBookingDto"})
    @JsonProperty("item")
    private ItemDto bookingItem;
    private UserDto booker;
    private Long userIdHeader;
    private Long itemId;
    private Long bookerId;
}
