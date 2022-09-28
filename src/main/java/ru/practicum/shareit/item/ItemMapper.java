package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ItemMapper {
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Autowired
    public ItemMapper(UserJpaRepository userJpaRepository, CommentJpaRepository commentJpaRepository,
                      CommentMapper commentMapper, @Lazy BookingMapper bookingMapper) {
        this.userJpaRepository = userJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
        this.commentMapper = commentMapper;
        this.bookingMapper = bookingMapper;
    }

    public ItemDto itemToDto(Item item, Boolean... isOwner) {
        ItemDto itemDto = ItemDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemDescription(item.getItemDescription())
                .isItemAvailable(item.getIsItemAvailable())
                .owner(item.getOwner())
                .nextBooking(null)
                .lastBooking(null)
                .commentsDtoList(commentJpaRepository.findAllByItemId(item.getItemId()).
                        stream().map(commentMapper::commentToDto).collect(Collectors.toList()))
                .build();

        if (isOwner.length == 1 && isOwner[0] && item.getBookingsSet() != null && !item.getBookingsSet().isEmpty()) {
            TreeSet<Booking> bookingsSortedSet = new TreeSet<>(Comparator.comparing(Booking::getBookingStart).reversed());

            bookingsSortedSet.addAll(item.getBookingsSet());

            if (bookingsSortedSet.first().getBookingStatus() != BookingStatus.REJECTED) {
                itemDto.setNextBooking(bookingMapper.bookingToDtoForItem(bookingsSortedSet.first()));
            }
            if (bookingsSortedSet.last().getBookingStatus() != BookingStatus.REJECTED) {
                itemDto.setLastBooking(bookingMapper.bookingToDtoForItem(bookingsSortedSet.last()));
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
