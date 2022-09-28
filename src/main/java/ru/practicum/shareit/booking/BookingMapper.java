package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.user.UserMapper;

@Service
public class BookingMapper {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;
    private final ItemJpaRepository itemJpaRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserJpaRepository userJpaRepository, UserMapper userMapper,
                         ItemJpaRepository itemJpaRepository, ItemMapper itemMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
        this.itemJpaRepository = itemJpaRepository;
        this.itemMapper = itemMapper;
    }

    public BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .bookingStart(booking.getBookingStart())
                .bookingEnd(booking.getBookingEnd())
                .bookingStatus(booking.getBookingStatus())
                .bookingItem(itemMapper.itemToDto(booking.getBookingItem()))
                .booker(userMapper.userToDto(booking.getBooker()))
                .build();
    }

    public BookingDto bookingToDtoForItem(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .bookingStart(booking.getBookingStart())
                .bookingEnd(booking.getBookingEnd())
                .bookingStatus(booking.getBookingStatus())
                .booker(userMapper.userToDto(booking.getBooker()))
                .bookerId(booking.getBooker().getUserId())
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
