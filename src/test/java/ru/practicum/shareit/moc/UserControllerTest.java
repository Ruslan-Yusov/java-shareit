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
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoAdd;
import ru.practicum.shareit.user.dto.UserDtoRead;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private UserRepository userRepository;

    @SneakyThrows
    @Test
    void getEmptyUserList() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findByIdUser() {
        UserDtoRead expected = new UserDtoRead(1, "name", "email@mail.ru");
        when(userService.getUserDto(any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer requestedId = invocation.getArgument(0);
                    if (requestedId == 100) {
                        throw new ResourceNotFoundException("no data found");
                    } else {
                        return new UserDtoRead(requestedId, "name", "email@mail.ru");
                    }
                });

        Integer userId = 1;
        String actualJson = mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        UserDtoRead actual = mapper.readValue(actualJson, UserDtoRead.class);
        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());

        userId = 100;
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createUserMocked() {
        UserDtoRead expected = new UserDtoRead(1, "name", "email@mail.ru");
        when(userService.addUser(new UserDtoAdd("name", "email@mail.ru")))
                .thenReturn(expected);

        String actual = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assertions.assertEquals(mapper.writeValueAsString(expected), actual);
    }

    @SneakyThrows
    @Test
    void createInvalidUser() {
        UserDtoAdd userDtoAdd = new UserDtoAdd(" ", "email@mail.ru");
        when(userService.addUser(new UserDtoAdd(" ", "email@mail.ru")))
                .thenThrow(BadRequestException.class);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDtoAdd)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser() {
        UserDtoRead userDtoRead = new UserDtoRead(1, "name", "email@mail.ru");
        when(userService.updateUser(new UserDtoUpdate("name", "email@mail.ru"), 1))
                .thenReturn(userDtoRead);

        String actual = mockMvc.perform(patch("/users/{id}", userDtoRead.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDtoRead)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getContentAsString();

        Assertions.assertEquals(mapper.writeValueAsString(userDtoRead), actual);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        doThrow(new ResourceNotFoundException("xxx")).when(userService).deleteUser(eq(100));
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/users/{id}", 100))
                .andExpect(status().isNotFound());
    }

}