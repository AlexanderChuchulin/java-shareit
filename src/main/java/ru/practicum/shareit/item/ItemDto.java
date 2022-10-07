package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.abstraction.EntityDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @JsonInclude
    private BookingDtoForItem nextBooking;
    @JsonInclude
    private BookingDtoForItem lastBooking;
    @JsonProperty("comments")
    private List<CommentDtoForItem> commentsDtoForItemList;
    private Long requestId;
    private Long userIdHeader;

    @JsonInclude
    @Getter
    public static class BookingDtoForItem {
        private final Long id;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final BookingStatus bookingStatus;
        private final Long bookerId;

        public BookingDtoForItem(Booking booking) {
            id = booking.getBookingId();
            start = booking.getBookingStart();
            end = booking.getBookingEnd();
            bookingStatus = booking.getBookingStatus();
            bookerId = booking.getBooker().getUserId();
        }
    }

    @JsonInclude
    @Builder
    @Getter
    public static class CommentDtoForItem {
        private Long id;
        private String text;
        private LocalDateTime created;
        private String authorName;

        public static List<CommentDtoForItem> createCommentsDtoForItemList(List<Comment> commentsList) {
            List<CommentDtoForItem> commentsDtoForItemList = new ArrayList<>();

            for (Comment comment : commentsList) {
                commentsDtoForItemList.add(CommentDtoForItem.builder()
                        .id(comment.getCommentId())
                        .text(comment.getCommentText())
                        .created(comment.getCommentDate())
                        .authorName(comment.getAuthor().getUserName())
                        .build());
            }
            return commentsDtoForItemList;
        }
    }
}
