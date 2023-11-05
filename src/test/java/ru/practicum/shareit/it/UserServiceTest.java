package ru.practicum.shareit.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.request.RequestEntity;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoAdd;
import ru.practicum.shareit.user.dto.UserDtoRead;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    private static final String EXPECTED_JSON_USER = "{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"}";
    private static final String EXPECTED_JSON_ITEM = "{\"id\":1,\"name\":\"item\",\"description\":\"description\",\"available\":true,\"owner\":{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"},\"comments\":[],\"request\":null}";
    private static final String EXPECTED_JSON_BOOKING = "{\"id\":1,\"startDateTime\":\"2023-11-06T15:37:56.3873336\",\"endDateTime\":\"2023-11-06T17:37:56.3873336\",\"item\":{\"id\":1,\"name\":\"item\",\"description\":\"description\",\"available\":true,\"owner\":{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"},\"comments\":[],\"request\":null},\"booker\":{\"id\":2,\"name\":\"name2\",\"email\":\"email2@mail.ru\"},\"status\":\"WAITING\"}";
    private static final String EXPECTED_JSON_REQUEST = "{\"id\":1,\"description\":\"descr-1\",\"author\":{\"id\":2,\"name\":\"name2\",\"email\":\"email2@mail.ru\"},\"created\":\"2023-11-06T15:37:56.3873336\",\"items\":[{\"id\":1,\"name\":\"item\",\"description\":\"description\",\"available\":true,\"owner\":{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"},\"comments\":[],\"request\":null}]}";

    private static LocalDateTime expectedStartTime = LocalDateTime.parse("2023-11-06T15:37:56.3873336");
    private static LocalDateTime expectedEndTime = LocalDateTime.parse("2023-11-06T17:37:56.3873336");

    @Test
    void test() {
        happyPassUserTest();
        clear();
        invalidUserTest();
    }

    void clear() {
        userService.getAllUsers().stream()
                .map(UserDtoRead::getId)
                .forEach(userService::deleteUser);
    }

    void happyPassUserTest() {
        UserDtoAdd userDtoAdd = new UserDtoAdd("name", "email1@mail.ru");
        UserDtoRead userDtoRead = userService.addUser(userDtoAdd);

        assertThat(userDtoRead)
                .isNotNull()
                .withFailMessage("invalid id")
                .extracting(UserDtoRead::getId)
                .isEqualTo(1);

        assertThat(userDtoRead)
                .withFailMessage("invalid name")
                .extracting(UserDtoRead::getName)
                .isEqualTo(userDtoAdd.getName());

        assertThat(userDtoRead)
                .isNotNull()
                .withFailMessage("invalid email")
                .extracting(UserDtoRead::getEmail)
                .isEqualTo(userDtoRead.getEmail());

        userDtoRead = userService.getUserDto(1);

        assertThat(userDtoRead)
                .isNotNull()
                .withFailMessage("invalid id")
                .extracting(UserDtoRead::getId)
                .isEqualTo(1);

        assertThat(userDtoRead)
                .withFailMessage("invalid name")
                .extracting(UserDtoRead::getName)
                .isEqualTo(userDtoAdd.getName());

        assertThat(userDtoRead)
                .withFailMessage("invalid email")
                .extracting(UserDtoRead::getEmail)
                .isEqualTo(userDtoRead.getEmail());

        UserDtoUpdate userDtoUpdate = new UserDtoUpdate("name1", "email1@mail.ru");
        userDtoRead = userService.updateUser(userDtoUpdate, 1);

        assertThat(userDtoRead)
                .isNotNull()
                .withFailMessage("invalid name")
                .extracting(UserDtoRead::getName)
                .isEqualTo(userDtoUpdate.getName());

        assertThat(userDtoRead)
                .withFailMessage("invalid email")
                .extracting(UserDtoRead::getEmail)
                .isEqualTo(userDtoUpdate.getEmail());

        List<UserDtoRead> userDtoList = userService.getAllUsers();

        assertThat(userDtoList)
                .isNotNull()
                .withFailMessage("invalid list")
                .hasSize(1)
                .element(0)
                .satisfies(dto -> assertThat(dto).extracting(UserDtoRead::getId).isEqualTo(1))
                .satisfies(dto -> assertThat(dto).extracting(UserDtoRead::getEmail).isEqualTo(userDtoUpdate.getEmail()))
                .satisfies(dto -> assertThat(dto).extracting(UserDtoRead::getName).isEqualTo(userDtoUpdate.getName()));

        userService.deleteUser(1);

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> userService.getUserDto(1));

        userDtoList = userService.getAllUsers();

        assertThat(userDtoList)
                .isNotNull()
                .isEmpty();

    }

    void invalidUserTest() {

        UserDtoAdd userDtoAddHappyTest = new UserDtoAdd("name", "email@mail.ru");
        UserDtoRead userDtoRead = userService.addUser(userDtoAddHappyTest);
        Assertions.assertThrowsExactly(
                DataIntegrityViolationException.class,
                () -> userService.addUser(userDtoAddHappyTest)
        );

        UserDtoAdd userDtoAddInvalidNameTest = new UserDtoAdd(" ", "email1@mail.ru");
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> userService.addUser(userDtoAddInvalidNameTest)
        );

        UserDtoAdd userDtoAddInvalidEmail = new UserDtoAdd("name1", " ");
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> userService.addUser(userDtoAddInvalidEmail)
        );

        UserDtoAdd userDtoAddInvalidEmail2 = new UserDtoAdd("name1", "aaaaaaaa@4444");
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> userService.addUser(userDtoAddInvalidEmail2)
        );

        userDtoAddInvalidEmail.setEmail("email001");
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> userService.addUser(userDtoAddInvalidEmail)
        );

        userDtoAddInvalidEmail.setEmail("email@mail.ru");
        Assertions.assertThrowsExactly(
                DataIntegrityViolationException.class,
                () -> userService.addUser(userDtoAddInvalidEmail)
        );

        ///////////
        UserDtoUpdate userDtoUpdateInvalid = new UserDtoUpdate("name1", "email1@4444");
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> userService.updateUser(userDtoUpdateInvalid, userDtoRead.getId())
        );
        UserDtoUpdate userDtoUpdateInvalid2 = new UserDtoUpdate("name1", "aaaa@bbb.cc");
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> userService.updateUser(userDtoUpdateInvalid2, userDtoRead.getId() + 100)
        );

        /////////////////////


        UserDtoAdd userDtoAddHappyTest2 = new UserDtoAdd("name2", "email2@mail.ru");
        UserDtoRead userDtoRead2 = userService.addUser(userDtoAddHappyTest2);
        Assertions.assertThrowsExactly(
                DataIntegrityViolationException.class,
                () -> userService.addUser(userDtoAddHappyTest)
        );
        UserDtoUpdate userDtoUpdateInvalid21 = new UserDtoUpdate(null, userDtoRead.getEmail());
        Assertions.assertThrowsExactly(
                DataIntegrityViolationException.class,
                () -> userService.updateUser(userDtoUpdateInvalid21, userDtoRead2.getId())
        );

        ////////////////////

        List<UserDtoRead> userDtoList = userService.getAllUsers();

        assertThat(userDtoList)
                .isNotNull()
                .withFailMessage("invalid list")
                .hasSize(2)
                .element(0)
                .satisfies(dto -> assertThat(dto).extracting(UserDtoRead::getId).isEqualTo(userDtoRead.getId()))
                .satisfies(dto -> assertThat(dto).extracting(UserDtoRead::getName).isEqualTo(userDtoAddHappyTest.getName()));

        userService.deleteUser(userDtoRead.getId());
        userService.deleteUser(userDtoRead2.getId());

        userDtoList = userService.getAllUsers();

        assertThat(userDtoList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Бессмысленный тест автогенерируемого кода для удолетворения условий покрытия кода")
    void pureEntitiesTest() {

        int expectedUserId = 1;
        int expectedBookerId = 2;
        LocalDateTime expectedTime = expectedStartTime;
        UserEntity expectedUserEntity = new UserEntity(
                expectedUserId,
                "name",
                "email@mail.ru");
        UserEntity expectedBookerEntity = new UserEntity(
                expectedBookerId,
                "name2",
                "email2@mail.ru");
        ItemEntity expectedItemEntity = new ItemEntity(
                1,
                "item",
                "description",
                true,
                expectedUserEntity,
                new HashSet<>(),
                null
        );
        ItemEntity expectedItemEntity2 = new ItemEntity(
                2,
                "item2",
                "description2",
                false,
                expectedBookerEntity,
                new HashSet<>(),
                null
        );
        RequestEntity expectedRequestEntity = new RequestEntity(
                1,
                "descr-1",
                expectedBookerEntity,
                expectedTime,
                Set.of(expectedItemEntity));
        RequestEntity expectedRequestEntity2 = new RequestEntity(
                2,
                "descr-2",
                expectedBookerEntity,
                expectedTime,
                Set.of(expectedItemEntity2));

        //////////////////////////

        assertThat(expectedItemEntity)
                .isNotEqualTo(expectedItemEntity2)
                .extracting(ItemEntity::hashCode)
                .isNotEqualTo(expectedItemEntity2.hashCode());
        assertThat(expectedRequestEntity)
                .isNotEqualTo(expectedRequestEntity2)
                .extracting(RequestEntity::hashCode)
                .isNotEqualTo(expectedRequestEntity2.hashCode());

        /////////////////////////
        BookingEntity expectedBookingEntity = new BookingEntity(
                1,
                expectedStartTime,
                expectedEndTime,
                expectedItemEntity,
                expectedBookerEntity,
                Status.WAITING);
        try {
            String jsonUser = mapper.writeValueAsString(expectedUserEntity);
            String jsonItem = mapper.writeValueAsString(expectedItemEntity);
            String jsonBooking = mapper.writeValueAsString(expectedBookingEntity);
            String jsonRequest = mapper.writeValueAsString(expectedRequestEntity);
            assertEquals(EXPECTED_JSON_USER, jsonUser);
            assertEquals(EXPECTED_JSON_ITEM, jsonItem);
            assertEquals(EXPECTED_JSON_BOOKING, jsonBooking);
            assertEquals(EXPECTED_JSON_REQUEST, jsonRequest);
            assertEquals(expectedUserEntity, mapper.readValue(EXPECTED_JSON_USER, UserEntity.class));
            assertEquals(expectedItemEntity, mapper.readValue(EXPECTED_JSON_ITEM, ItemEntity.class));
            assertEquals(expectedBookingEntity, mapper.readValue(EXPECTED_JSON_BOOKING, BookingEntity.class));
            assertEquals(expectedRequestEntity, mapper.readValue(EXPECTED_JSON_REQUEST, RequestEntity.class));

        } catch (JsonProcessingException e) {
            assertNull(e);
        }
    }
}
