package ru.practicum.shareit.moc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoRead;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemControllerTest {

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
    void getItem() {
        ItemDtoRead expected = new ItemDtoRead(1,
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
        when(itemService.getAllItem(any(Integer.class)))
                .thenAnswer(invocationOnMock -> {
                    Integer requestedId = invocationOnMock.getArgument(0);
                    if (requestedId == 100) {
                        throw new ResourceNotFoundException("not item found");
                    } else {
                        return List.of(new ItemDtoRead(1,
                                "item",
                                new UserDtoRead(requestedId,
                                        "name",
                                        "email@mail.ru"),
                                "description",
                                true,
                                null,
                                null,
                                new HashSet<>(),
                                1));
                    }
                });

        Integer userId = 1;
        String actualJson = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, ?> actual = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(userId, ((Map<?, ?>)actual.get("owner")).get("id"));
        Assertions.assertEquals(expected.getId(), actual.get("id"));

        userId = 100;
        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void search() {
        ItemDtoRead expected = new ItemDtoRead(1,
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
        when(itemService.findBySubstr(any(String.class)))
                .thenAnswer(invocationOnMock -> {
                    String nameOrDescription = invocationOnMock.getArgument(0);
                    if (nameOrDescription.isBlank()) {
                       return new ArrayList<>();
                    } else if (nameOrDescription.equals(expected.getName())
                            || nameOrDescription.equals(expected.getDescription())) {
                        return List.of(new ItemDtoRead(1,
                                "item",
                                new UserDtoRead(1,
                                        "name",
                                        "email@mail.ru"),
                                "description",
                                true,
                                null,
                                null,
                                new HashSet<>(),
                                1));
                    } else {
                      return new ArrayList<>();
                    }
                });

        String name = "item";
        Integer userId = 1;
        String actualJson = mockMvc.perform(get("/items/search")
                .header("X-Sharer-User-Id", userId)
                .param("text", name))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, ?> actual = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(expected.getName(), actual.get("name"));

        name = "description";
        userId = 1;
        actualJson = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", name))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, ?> actual2 = (Map<String, ?>)mapper.readValue(actualJson, List.class)
                .get(0);
        Assertions.assertEquals(expected.getDescription(), actual2.get("description"));

        name = " ";
        userId = 1;
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", name))
                .andExpect(status().isOk())
                .andReturn()
                .getFlashMap();
    }

    @Test
    @SneakyThrows
    void getItemById() {
        ItemDtoRead expected = new ItemDtoRead(1,
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
        when(itemService.getByIdItem(any(Integer.class), any(Integer.class)))
                .thenAnswer(invocationOnMock -> {
                    Integer itemId = invocationOnMock.getArgument(0);
                    Integer userId = invocationOnMock.getArgument(0);
                    if (itemId == 100 || userId == 100) {
                        throw new ResourceNotFoundException("not item or user found");
                    } else {
                        return new ItemDtoRead(itemId,
                                "item",
                                new UserDtoRead(userId,
                                        "name",
                                        "email@mail.ru"),
                                "description",
                                true,
                                null,
                                null,
                                new HashSet<>(),
                                1);
                    }
                });

        Integer userId = 1;
        Integer itemId = 1;
        String actualJson = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        ItemDtoRead actual = mapper.readValue(actualJson, ItemDtoRead.class);
        Assertions.assertEquals(userId, actual.getOwner().getId());
        Assertions.assertEquals(itemId, actual.getId());
        Assertions.assertEquals(expected.getId(), actual.getId());

        userId = 100;
        itemId = 100;
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void addItem() {
        ItemDtoAdd itemDtoAdd = new ItemDtoAdd("item",
                "description",
                true,
                1,
                1);
        ItemDtoRead expected = new ItemDtoRead(1,
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
        when(itemService.addItem(itemDtoAdd, 1))
                .thenReturn(expected);

        Integer userId = 1;
        String actual = mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType("application/json")
                .content(mapper.writeValueAsString(itemDtoAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assertions.assertEquals(mapper.writeValueAsString(expected), actual);

        itemDtoAdd.setAvailable(null);
        when(itemService.addItem(itemDtoAdd, 1))
                .thenThrow(BadRequestException.class);


    }

    @Test
    @SneakyThrows
    void updateItem() {
        ItemDtoUpdate update = new ItemDtoUpdate("item", "description", true);
        ItemDtoRead expected = new ItemDtoRead(1,
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
        when(itemService.updateItem(update, 1, 1))
                .thenReturn(expected);

        Integer userId = 1;
        Integer itemId = 1;
        String actual = mockMvc.perform(patch("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assertions.assertEquals(mapper.writeValueAsString(expected), actual);
    }

    @Test
    @SneakyThrows
    void addItemComment() {
        CommentDtoAdd commentDtoAdd = new CommentDtoAdd("comment");
        CommentDtoRead commentDtoRead = new CommentDtoRead(1,
                "comment",
                "name",
                LocalDateTime.now());
        when(itemService.addItemComment(1, 1, commentDtoAdd))
                .thenReturn(commentDtoRead);

        Integer userId = 1;
        Integer itemId = 1;
        String actual = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", userId)
                .contentType("application/json")
                .content(mapper.writeValueAsString(commentDtoAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assertions.assertEquals(mapper.writeValueAsString(commentDtoRead), actual);
    }
}