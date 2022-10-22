package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String entityName = "Booking";

    @PostMapping
    public ResponseEntity<Object> createBookingController(@RequestHeader("X-Sharer-User-Id") long userIdHeader,
                                                          @RequestBody @Validated BookingDto bookingDto) {
        log.info("Creating {} {}", entityName, bookingDto);
        return bookingClient.createBookingClient(userIdHeader, bookingDto);
    }

    @GetMapping(value = {"", "/owner", "/{bookingId}"})
    public ResponseEntity<Object> getBookingsController(@PathVariable(required = false) Long bookingId,
                                                        @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") String from,
                                                        @Positive @RequestParam(value = "size", required = false) String size,
                                                        @RequestParam(value = "state", required = false, defaultValue = "ALL") String bookingState,
                                                        @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userIdHeader) {

        if (bookingId == null) {
            if (!BookingState.stateCheck(bookingState)) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Unknown state: " + bookingState));
            }

            if (ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toString().contains("owner")) {
                bookingId = -111222333L;
            }

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("from", from);
            parameters.put("size", size);
            parameters.put("state", bookingState);

            log.info("Get {}", entityName + "s");
            return bookingClient.getBookingClient(bookingId, userIdHeader, parameters);
        }
        log.info("Get {} by id={}", entityName, bookingId);
        return bookingClient.getBookingClient(bookingId, userIdHeader, null);
    }

    @PatchMapping(path = "/{bookingId}", params = {"approved"})
    public ResponseEntity<Object> updateBookingController(@RequestHeader(value = "X-Sharer-User-Id") long userIdHeader,
                                                          @RequestParam("approved") Boolean isBookingApproved,
                                                          @PathVariable long bookingId) {
        Map<String, Object> parameters = Map.of("approved", isBookingApproved);

        log.info("Updating {}", entityName);
        return bookingClient.updateBookingClient("/" + bookingId + "?approved=" + isBookingApproved, userIdHeader, parameters);
    }

    @DeleteMapping(value = {"", "/{bookingId}"})
    public ResponseEntity<Object> deleteUsersController(@PathVariable(required = false) Long bookingId) {
        String path = "";

        if (bookingId == null) {
            log.info("Delete {}", entityName + "s");
        } else {
            log.info("Delete {} by id={}", entityName, bookingId);
            path = "/" + bookingId;
        }
        return bookingClient.deleteBookingClient(path);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final String error;
    }

}


