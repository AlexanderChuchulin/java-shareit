package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.abstraction.CommonController;

@RestController
@RequestMapping("/bookings")
public class BookingController extends CommonController<BookingDto> {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
        shareItService = bookingService;
    }

    @GetMapping("/owner")
    public Object getBookingsByOwnerController(
            @RequestParam(value = "from", required = false, defaultValue = "0") String from,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String bookingState,
            @RequestHeader(value = "X-Sharer-User-Id") Long userIdHeader) {
        return bookingService.getEntityService(-111222333L, userIdHeader, from, size, bookingState);
    }

    @PatchMapping(path = "/{bookingId}", params = {"approved"})
    public BookingDto updateBookingController(@PathVariable Long bookingId, @RequestParam("approved") Boolean isBookingApproved,
                                               @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {
        return bookingService.updateBookingService(bookingId, userIdHeader, isBookingApproved);
    }
}
