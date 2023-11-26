package ru.practicum.shareitgw.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareitgw.booking.dto.BookingDtoAdd;
import ru.practicum.shareitgw.booking.dto.BookingDtoRead;
import ru.practicum.shareitgw.connector.BookingConnector;
import ru.practicum.shareitgw.exeption.BadRequestException;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * TODO Sprint add-bookings.
 */
@Service
public class BookingGwService {

    @Autowired
    private BookingConnector connector;

    public static class BookingDtoReadList extends ArrayList<BookingDtoRead> {
    }


    public List<BookingDtoRead> getAllUserBookings(Integer id, String state, Integer from, Integer size) {
        if (!Arrays.asList("CURRENT", "PAST", "FUTURE", "ALL", "REJECTED", "WAITING").contains(state)) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
        int from1 = ofNullable(from).orElse(0);
        int size1 = ofNullable(size).orElse(10000);
        if (from1 < 0 || size1 <= 0) {
            throw new BadRequestException("invalid paging");
        }
        Map<String, Object> queryParams = new HashMap<>();

        ofNullable(state).ifPresent(v -> queryParams.put("state", v));
        ofNullable(from1).ifPresent(v -> queryParams.put("from", v));
        ofNullable(size1).ifPresent(v -> queryParams.put("size", v));
        return connector.getUserBooking(id, queryParams);
    }

    public List<BookingDtoRead> getAllOwner(Integer id, String state, Integer from, Integer size) {
        if (!Arrays.asList("CURRENT", "PAST", "FUTURE", "ALL", "REJECTED", "WAITING").contains(state)) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
        int from1 = ofNullable(from).orElse(0);
        int size1 = ofNullable(size).orElse(10000);
        if (from1 < 0 || size1 <= 0) {
            throw new BadRequestException("invalid paging");
        }
        Map<String, Object> queryParams = new HashMap<>();

        ofNullable(state).ifPresent(v -> queryParams.put("state", v));
        ofNullable(from1).ifPresent(v -> queryParams.put("from", v));
        ofNullable(size1).ifPresent(v -> queryParams.put("size", v));
        return connector.getOwnerBooking(id, queryParams);
    }

    public BookingDtoRead getSpecificBooking(Integer idUserOrBooker, Integer idBooking) {
        return connector.getSpecialBooking(idUserOrBooker, idBooking);
    }

    public BookingDtoRead addBooking(BookingDtoAdd bookingDtoAdd, Integer idUser) {
        ofNullable(bookingDtoAdd.getItemId())
                .orElseThrow(() -> new BadRequestException("Не задан обязательный параметр ItemId"));
        ofNullable(bookingDtoAdd.getStart())
                .orElseThrow(() -> new BadRequestException("Не задан обязательный параметр Start или Start указан позже End"));
        if (LocalDateTime.now().isAfter(bookingDtoAdd.getStart())) {
            throw new BadRequestException("Start не может быть раньше текущей даты");
        }
        if (bookingDtoAdd.getStart().equals(bookingDtoAdd.getEnd())) {
            throw new BadRequestException("Start не может содержать End");
        }
        ofNullable(bookingDtoAdd.getEnd())
                .filter(en -> en.isAfter(bookingDtoAdd.getStart()))
                .orElseThrow(() -> new BadRequestException("Не задан обязательный параметр End или End указан раньше Start"));
        return connector.addBooking(idUser, bookingDtoAdd);
    }

    public BookingDtoRead updateBookingStatus(Integer idBooking,
                                              Integer idUser,
                                              boolean approved) {
        return connector.updateBookingStatus(idUser, idBooking, approved);
    }
}
