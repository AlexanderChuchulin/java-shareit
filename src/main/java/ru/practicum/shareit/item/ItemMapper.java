package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.Comparator;
import java.util.TreeSet;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class ItemMapper {
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Autowired
    public ItemMapper(UserJpaRepository userJpaRepository, CommentJpaRepository commentJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
    }

    public ItemDto itemToDto(Item item, Boolean... isOwner) {
        ItemDto itemDto = ItemDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemDescription(item.getItemDescription())
                .isItemAvailable(item.getIsItemAvailable())
                .owner(item.getOwner())
                .commentsDtoForItemList(ItemDto.CommentDtoForItem.createCommentsDtoForItemList(commentJpaRepository
                        .findAllByCommentItemItemId(item.getItemId(), Sort.by(DESC, "commentDate"))))
                .build();

        if (isOwner.length == 1 && isOwner[0] && item.getBookingsSet() != null && !item.getBookingsSet().isEmpty()) {
            TreeSet<Booking> bookingsSortedSet = new TreeSet<>(Comparator.comparing(Booking::getBookingStart).reversed());

            bookingsSortedSet.addAll(item.getBookingsSet());

            if (bookingsSortedSet.first().getBookingStatus() != BookingStatus.REJECTED) {
                itemDto.setNextBooking(new ItemDto.BookingDtoForItem(bookingsSortedSet.first()));
            }
            if (bookingsSortedSet.last().getBookingStatus() != BookingStatus.REJECTED) {
                itemDto.setLastBooking(new ItemDto.BookingDtoForItem(bookingsSortedSet.last()));
            }
        }
        return itemDto;
    }

    public Item dtoToItem(ItemDto itemDto, Long userIdHeader) {
        Long userIdOwner = userIdHeader;
        if (userIdHeader == null) {
            userIdOwner = 0L;
        }
        return Item.builder()
                .itemName(itemDto.getItemName())
                .itemDescription(itemDto.getItemDescription())
                .isItemAvailable(itemDto.getIsItemAvailable())
                .userIdHeader(userIdHeader)
                .owner(userJpaRepository.findById(userIdOwner).orElse(null))
                .build();
    }
}
