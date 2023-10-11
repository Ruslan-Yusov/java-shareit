package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingDtoRead;

import java.util.List;

/**
 * TODO Sprint add-bookings.
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
                                               String state) {
        return bookingService.getAllUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoRead> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(name = "state",
                                                       required = false,
                                                       defaultValue = "ALL") String state) {
        return bookingService.getAllOwner(userId, state);
    }

    @GetMapping("{bookingId}")
    public BookingDtoRead getSpecialBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @PathVariable("bookingId") Integer id) {
        return bookingService.getSpecificBooking(userId, id);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoRead updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable("bookingId") Integer id,
                                              @RequestParam(name = "approved", required = true) boolean accept) {
        return bookingService.updateBookingStatus(id, userId, accept);
    }

    @PostMapping("")
    public BookingDtoRead addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @RequestBody BookingDtoAdd bookingDtoAdd
    ) {
        return bookingService.addBooking(bookingDtoAdd, userId);
    }
}
