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
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.RequestDtoAdd;
import ru.practicum.shareit.request.RequestDtoRead;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getAllRequest() {
        RequestDtoRead expected = new RequestDtoRead(1,
                "description",
                1,
                LocalDateTime.now(),
                null);
        when(requestService.getAllRequest(any(Integer.class)))
                .thenAnswer(invocationOnMock -> {
                    Integer userId = invocationOnMock.getArgument(0);
                    if (userId == 100) {
                        throw new ResourceNotFoundException("not user found");
                    } else {
                        return List.of(expected);
                    }
                });

        int userId = 1;
        String actualJson = mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, ?> actual = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(expected.getId(), actual.get("id"));

        userId = 100;
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getById() {
        RequestDtoRead expected = new RequestDtoRead(1,
                "description",
                1,
                LocalDateTime.now(),
                null);
        when(requestService.getById(any(Integer.class), any(Integer.class)))
                .thenAnswer(invocationOnMock -> {
                    Integer userId = invocationOnMock.getArgument(0);
                    Integer requestId = invocationOnMock.getArgument(0);
                    if (userId == 100 || requestId == 100) {
                        throw new ResourceNotFoundException("not user found");
                    } else {
                        return expected;
                    }
                });

        int userId = 1;
        int requestId = 1;
        String actualJson = mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        RequestDtoRead actual = mapper.readValue(actualJson, RequestDtoRead.class);

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getAuthorId(), actual.getAuthorId());

        userId = 100;
        requestId = 100;
        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

    }

    @Test
    @SneakyThrows
    void getPag() {
        RequestDtoRead expected = new RequestDtoRead(1,
                "description",
                1,
                LocalDateTime.now(),
                null);
        when(requestService.getAllRequestPag(any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenAnswer(invocationOnMock -> {
                    Integer userId = invocationOnMock.getArgument(0);
                    if (userId == 100) {
                        throw new ResourceNotFoundException("not user found");
                    } else {
                        return List.of(expected);
                    }
                });

        int userId = 1;
        String actualJson = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, ?> actual = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(expected.getId(), actual.get("id"));

        userId = 100;
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void addRequest() {
        RequestDtoRead expected = new RequestDtoRead(1,
                "description",
                1,
                LocalDateTime.now(),
                null);
        RequestDtoAdd requestDtoAdd = new RequestDtoAdd("description", 1);

        when(requestService.addRequest(1, requestDtoAdd))
                .thenReturn(expected);

        Integer userId = 1;
        String actual = mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDtoAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assertions.assertEquals(mapper.writeValueAsString(expected), actual);
    }
}