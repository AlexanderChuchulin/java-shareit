package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.user.UserJpaRepository;

@Service
public class BookingMapper {
    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;

    @Autowired
    public BookingMapper(UserJpaRepository userJpaRepository, ItemJpaRepository itemJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.itemJpaRepository = itemJpaRepository;
    }

    public BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .bookingStart(booking.getBookingStart())
                .bookingEnd(booking.getBookingEnd())
                .bookingStatus(booking.getBookingStatus())
                .bookingItem(new BookingDto.ItemDtoForBooking(booking.getBookingItem()))
                .booker(new BookingDto.UserDtoForBooking(booking.getBooker().getUserId()))
                .build();
    }

    public Booking dtoToBooking(BookingDto bookingDto, Long userIdHeader) {
        return Booking.builder()
                .bookingId(bookingDto.getBookingId())
                .bookingStart(bookingDto.getBookingStart())
                .bookingEnd(bookingDto.getBookingEnd())
                .bookingStatus(bookingDto.getBookingStatus())
                .bookingItem(itemJpaRepository.findById(bookingDto.getItemId()).orElse(null))
                .booker(userJpaRepository.findById(userIdHeader).orElse(null))
                .userIdHeader(userIdHeader)
                .build();
    }
}
