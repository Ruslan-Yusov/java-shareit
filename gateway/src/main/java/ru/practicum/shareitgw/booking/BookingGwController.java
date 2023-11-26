package ru.practicum.shareitgw.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgw.booking.dto.BookingDtoAdd;
import ru.practicum.shareitgw.booking.dto.BookingDtoRead;

import java.util.List;

/**
 * Booking Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingGwController {

    private final BookingGwService bookingGwService;

    @GetMapping("")
    public List<BookingDtoRead> getUserBooking(@RequestHeader("X-Sharer-User-Id")
                                               Integer userId,
                                               @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                               String state,
                                               @RequestParam(name = "from", required = false)
                                               Integer from,
                                               @RequestParam(name = "size", required = false)
                                               Integer size) {
        return bookingGwService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoRead> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                                @RequestParam(name = "from", required = false) Integer from,
                                                @RequestParam(name = "size", required = false) Integer size) {
        return bookingGwService.getAllOwner(userId, state, from, size);
    }

    @GetMapping("{bookingId}")
    public BookingDtoRead getSpecialBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @PathVariable("bookingId") Integer id) {
        return bookingGwService.getSpecificBooking(userId, id);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoRead updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable("bookingId") Integer id,
                                              @RequestParam(name = "approved") boolean approved) {
        return bookingGwService.updateBookingStatus(id, userId, approved);
    }

    @PostMapping("")
    public BookingDtoRead addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestBody BookingDtoAdd bookingDtoAdd
    ) {
        return bookingGwService.addBooking(bookingDtoAdd, userId);
    }
}
