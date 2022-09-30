package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    @Query("select booking from Booking booking where booking.booker.userId = ?1 and booking.bookingStatus = ?2 " +
            "order by booking.bookingStart desc")
    List<Booking> findAllByBookerIdAndStatus(Long userIdHeader, BookingStatus bookingStatus);

    @Query("select booking from Booking booking where booking.booker.userId = ?1 order by booking.bookingStart desc")
    List<Booking> findAllByBookerIdAndStateAll(Long userIdHeader);

    @Query("select booking from Booking booking where booking.booker.userId = ?1 and booking.bookingEnd < ?2 " +
            "order by booking.bookingStart desc")
    List<Booking> findAllByBookerIdAndStatePast(Long userIdHeader, LocalDateTime nowTime);

    @Query("select booking from Booking booking where booking.booker.userId = ?1 and booking.bookingStart < ?2 " +
            "and booking.bookingEnd > ?2 order by booking.bookingStart desc")
    List<Booking> findAllByBookerIdAndStateCurrent(Long userIdHeader, LocalDateTime nowTime);

    @Query("select booking from Booking booking where booking.booker.userId = ?1 and booking.bookingStart > ?2 " +
            "order by booking.bookingStart desc")
    List<Booking> findAllByBookerIdAndStateFuture(Long userIdHeader, LocalDateTime nowTime);

    @Query("select booking from Booking booking left join Item as item on booking.bookingItem.itemId = item.itemId " +
            "where item.owner.userId = ?1 and booking.bookingStatus = ?2 order by booking.bookingStart desc")
    List<Booking> findAllByItemsOwnerIdAndStatus(Long userIdHeader, BookingStatus bookingStatus);

    @Query("select booking from Booking booking left join Item as item on booking.bookingItem.itemId = item.itemId " +
            "where item.owner.userId = ?1 order by booking.bookingStart desc")
    List<Booking> findAllByItemsOwnerIdAndStateAll(Long userIdHeader);

    @Query("select booking from Booking booking left join Item as item on booking.bookingItem.itemId = item.itemId " +
            "where item.owner.userId = ?1 and booking.bookingEnd < ?2 order by booking.bookingStart desc")
    List<Booking> findAllByItemsOwnerIdAndStatePast(Long userIdHeader, LocalDateTime nowTime);

    @Query("select booking from Booking booking left join Item as item on booking.bookingItem.itemId = item.itemId " +
            "where item.owner.userId = ?1 and booking.bookingStart < ?2 and booking.bookingEnd > ?2 order by booking.bookingStart desc")
    List<Booking> findAllByItemsOwnerIdAndStateCurrent(Long userIdHeader, LocalDateTime nowTime);

    @Query("select booking from Booking booking left join Item as item on booking.bookingItem.itemId = item.itemId " +
            "where item.owner.userId = ?1 and booking.bookingStart > ?2 order by booking.bookingStart desc")
    List<Booking> findAllByItemsOwnerIdAndStateFuture(Long userIdHeader, LocalDateTime nowTime);

    @Query("select booking from Booking booking where booking.booker.userId = ?2 and booking.bookingStatus = 'APPROVED' " +
            "and booking.bookingItem.itemId = ?1 and booking.bookingEnd < ?3")
    List<Booking> findAllByBookerIdAndItemIdAndTime(Long itemId, Long userIdHeader, LocalDateTime currentTime);
}
