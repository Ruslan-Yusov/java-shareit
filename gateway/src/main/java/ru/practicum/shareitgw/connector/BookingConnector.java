package ru.practicum.shareitgw.connector;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.shareitgw.booking.BookingGwService;
import ru.practicum.shareitgw.booking.dto.BookingDtoAdd;
import ru.practicum.shareitgw.booking.dto.BookingDtoRead;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingConnector {
    private final BaseClient client;
    @Value("${shareit-gateway.services.booking}")
    private String baseUrl;

    public List<BookingDtoRead> getUserBooking(Integer userId, Map<String, Object> queryParams) {
        return client.get(baseUrl, userId, null, queryParams, BookingGwService.BookingDtoReadList.class).getBody();
    }

    public List<BookingDtoRead> getOwnerBooking(Integer userId, Map<String, Object> queryParams) {
        return client.get(baseUrl.concat("/owner"), userId, null, queryParams, BookingGwService.BookingDtoReadList.class).getBody();
    }

    public BookingDtoRead getSpecialBooking(Integer userId, Integer bookingId) {
        return client.get(baseUrl.concat("/{bookingId}"), userId, Map.of("bookingId", bookingId), BookingDtoRead.class).getBody();
    }

    public BookingDtoRead addBooking(Integer userId, BookingDtoAdd bookingDtoAdd) {
        return client.post(baseUrl, userId, bookingDtoAdd, BookingDtoRead.class).getBody();
    }

    public BookingDtoRead updateBookingStatus(Integer userId, Integer bookingId, boolean approved) {
        return client.patch(
                baseUrl.concat("/{bookingId}"),
                userId,
                Map.of("bookingId", bookingId),
                Map.of("approved", approved),
                null,
                BookingDtoRead.class
        ).getBody();
    }
}
