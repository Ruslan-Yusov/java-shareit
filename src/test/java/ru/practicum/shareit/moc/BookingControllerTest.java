package ru.practicum.shareit.moc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingDtoRead;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoRead;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private BookingEntity bookingEntity;

    @Test
    @SneakyThrows
    void getUserBooking() {
        ItemDtoRead itemDtoRead = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        BookingDtoRead expected = new BookingDtoRead(1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                itemDtoRead,
                new UserDtoRead(1,
                        "name",
                        "emqil@mail.ru"),
                Status.APPROVED);
        when(bookingService.getAllUserBookings(1, "APPROVED", 1, 1))
                .thenReturn(List.of(expected));

        Integer userId = 1;
        String actualJson = mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .param("state", "APPROVED")
                .param("from", String.valueOf(1))
                .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, ?> actual = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(userId, ((Map<?, ?>)actual.get("booker")).get("id"));

        when(bookingService.getAllUserBookings(100, "APPROVED", 1, 1))
                .thenThrow(ResourceNotFoundException.class);
    }

    @Test
    @SneakyThrows
    void getOwnerBooking() {
        ItemDtoRead itemDtoRead = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        BookingDtoRead expected = new BookingDtoRead(1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                itemDtoRead,
                new UserDtoRead(1,
                        "name",
                        "emqil@mail.ru"),
                Status.APPROVED);
        when(bookingService.getAllOwner(1, "APPROVED", 1, 1))
                .thenReturn(List.of(expected));

        Integer userId = 1;
        String actualJson = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "APPROVED")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, ?> actual = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(userId, ((Map<?, ?>)actual.get("booker")).get("id"));
        Assertions.assertEquals(expected.getId(), actual.get("id"));

        when(bookingService.getAllUserBookings(100, "APPROVED", 1, 1))
                .thenThrow(ResourceNotFoundException.class);
    }

    @Test
    @SneakyThrows
    void getSpecialBooking() {
        ItemDtoRead itemDtoRead = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        BookingDtoRead expected = new BookingDtoRead(1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                itemDtoRead,
                new UserDtoRead(1,
                        "name",
                        "emqil@mail.ru"),
                Status.APPROVED);
        Integer userId = 1;
        Integer bookingId = 1;
        when(bookingService.getSpecificBooking(userId, bookingId))
                .thenReturn(expected);

        String actualJson = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        BookingDtoRead actual = mapper.readValue(actualJson, BookingDtoRead.class);
        Assertions.assertEquals(expected.getId(), actual.getId());
    }

    @Test
    @SneakyThrows
    void updateBookingStatus() {
        ItemDtoRead itemDtoRead = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        BookingDtoRead expected = new BookingDtoRead(1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                itemDtoRead,
                new UserDtoRead(1,
                        "name",
                        "emqil@mail.ru"),
                Status.WAITING);

        when(bookingService.updateBookingStatus(any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    Integer userId = invocationOnMock.getArgument(0);
                    Integer bookingId = invocationOnMock.getArgument(0);
                    if (userId == 100 || bookingId == 100) {
                        throw new ResourceNotFoundException("not user or booker found");
                    } else {
                        return new BookingDtoRead(bookingId,
                                LocalDateTime.now().minusDays(2),
                                LocalDateTime.now().minusDays(1),
                                itemDtoRead,
                                new UserDtoRead(userId,
                                        "name",
                                        "emqil@mail.ru"),
                                Status.APPROVED);
                    }
                });

        Integer userId = 1;
        Integer bookingId = 1;
        String actualJson = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        BookingDtoRead actual = mapper.readValue(actualJson, BookingDtoRead.class);
        Assertions.assertEquals(expected.getId(), actual.getId());
        expected.setStatus(Status.APPROVED);
        Assertions.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @SneakyThrows
    void addBooking() {
        ItemDtoRead itemDtoRead = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        BookingDtoAdd bookingDtoAdd = new BookingDtoAdd(1,
                null,
                null);
        BookingDtoRead expected = new BookingDtoRead(1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemDtoRead,
                new UserDtoRead(3,
                        "name3",
                        "emqil3@mail.ru"),
                Status.WAITING);
        Integer userId = 3;
        when(bookingService.addBooking(bookingDtoAdd, userId))
                .thenReturn(expected);

        String actualJson = mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        BookingDtoRead actual = mapper.readValue(actualJson, BookingDtoRead.class);
        Assertions.assertEquals(expected.getId(), actual.getId());
    }
}