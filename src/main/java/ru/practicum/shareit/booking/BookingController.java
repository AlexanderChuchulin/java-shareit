package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.CommonController;

@RestController
@RequestMapping("/bookings")
public class BookingController extends CommonController<Booking, BookingDto> {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
        shareItService = bookingService;
    }

    @GetMapping("/owner")
    private Object getBookingsByOwnerController(
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String bookingStatus,
            @RequestHeader(value = "X-Sharer-User-Id") Long userIdHeader) {
        return bookingService.getEntityService(-111222333L, userIdHeader, bookingStatus);
    }

    @PatchMapping(path = "/{bookingId}", params = {"approved"})
    private BookingDto updateEntityController(@PathVariable Long bookingId, @RequestParam("approved") Boolean isBookingApproved,
                                             @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return bookingService.updateBookingService(bookingId, userIdHeader, isBookingApproved);
    }
}
