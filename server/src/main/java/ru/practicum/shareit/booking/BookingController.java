package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingDtoRead;

import java.util.List;

/**
 * Booking Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("")
    public List<BookingDtoRead> getUserBooking(@RequestHeader("X-Sharer-User-Id")
                                               Integer userId,
                                               @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                               String state,
                                               @RequestParam(name = "from", required = false)
                                               Integer from,
                                               @RequestParam(name = "size", required = false)
                                               Integer size) {
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoRead> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(name = "state",
                                                       required = false,
                                                       defaultValue = "ALL") String state,
                                       @RequestParam(name = "from",
                                               required = false) Integer from,
                                                @RequestParam(name = "size",
                                                        required = false) Integer size) {
        return bookingService.getAllOwner(userId, state, from, size);
    }

    @GetMapping("{bookingId}")
    public BookingDtoRead getSpecialBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @PathVariable("bookingId") Integer id) {
        return bookingService.getSpecificBooking(userId, id);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoRead updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable("bookingId") Integer id,
                                              @RequestParam(name = "approved") boolean accept) {
        return bookingService.updateBookingStatus(id, userId, accept);
    }

    @PostMapping("")
    public BookingDtoRead addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestBody BookingDtoAdd bookingDtoAdd
    ) {
        return bookingService.addBooking(bookingDtoAdd, userId);
    }
}
