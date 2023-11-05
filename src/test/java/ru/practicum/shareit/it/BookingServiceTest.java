package ru.practicum.shareit.it;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingDtoRead;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.request.RequestEntity;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoRead;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void testBookingService() {
        int expectedUserId = 1;
        int expectedBookerId = 2;

        Map<String, BookingEntity> expectedBookings = Map.of(
                "CURRENT",  new BookingEntity(1, now().minusHours(1), now().plusHours(1), null, null, Status.WAITING),
                "PAST",     new BookingEntity(2, now().minusHours(1), now().plusHours(1), null, null, Status.WAITING),
                "FUTURE",   new BookingEntity(3, now().minusHours(1), now().plusHours(1), null, null, Status.WAITING),
                "ALL",      new BookingEntity(4, now().minusHours(1), now().plusHours(1), null, null, Status.WAITING),
                "REJECTED", new BookingEntity(5, now().minusHours(1), now().plusHours(1), null, null, Status.WAITING),
                "WAITING",  new BookingEntity(6, now().minusHours(1), now().plusHours(1), null, null, Status.WAITING)
        );
        UserDtoRead expectedOwner = new UserDtoRead(expectedUserId,
                "name",
                "email@mail.ru");
        UserDtoRead expectedBooker = new UserDtoRead(expectedBookerId,
                "name2",
                "email2@mail.ru");
        ItemDtoRead expectedItem = new ItemDtoRead(1,
                "item",
                expectedOwner,
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        ItemDtoRead expectedLockedItem = new ItemDtoRead(2,
                "locked",
                expectedOwner,
                "locked",
                false,
                null,
                null,
                new HashSet<>(),
                -1);
        UserEntity expectedUserEntity = new UserEntity(
                expectedOwner.getId(),
                expectedOwner.getName(),
                expectedOwner.getEmail());
        UserEntity expectedBookerEntity = new UserEntity(
                expectedBooker.getId(),
                expectedBooker.getName(),
                expectedBooker.getEmail());
        ItemEntity expectedItemEntity = new ItemEntity(
                expectedItem.getId(),
                expectedItem.getName(),
                expectedItem.getDescription(),
                expectedItem.getAvailable(),
                expectedUserEntity,
                new HashSet<>(),
                new RequestEntity() { { setId(expectedItem.getRequestId());
                    }
                }
        );
        Mockito.when(bookingRepository.findByBookerIdAndState(anyInt(), anyString()))
                .thenAnswer(invocation -> {
                            String state = invocation.getArgument(1);
                            return ofNullable(expectedBookings.getOrDefault(state, null))
                                    .map(List::of)
                                    .orElseGet(ArrayList::new)
                                    .stream()
                                    .peek(bookingEntity -> bookingEntity.setItem(expectedItemEntity))
                                    .peek(bookingEntity -> bookingEntity.setBooker(expectedBookerEntity))
                                    .collect(Collectors.toList());
                        }
                );
        Mockito.when(bookingRepository.findByOwnerIdAndState(anyInt(), anyString()))
                .thenAnswer(invocation -> {
                            String state = invocation.getArgument(1);
                            return ofNullable(expectedBookings.getOrDefault(state, null))
                                    .map(List::of)
                                    .orElseGet(ArrayList::new)
                                    .stream()
                                    .peek(bookingEntity -> bookingEntity.setItem(expectedItemEntity))
                                    .peek(bookingEntity -> bookingEntity.setBooker(expectedBookerEntity))
                                    .collect(Collectors.toList());
                        }
                );
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                            int id = invocation.getArgument(0);
                            return expectedBookings.values().stream()
                                    .filter(ent -> ent.getId() == id)
                                    .peek(bookingEntity -> bookingEntity.setItem(expectedItemEntity))
                                    .peek(bookingEntity -> bookingEntity.setBooker(expectedBookerEntity))
                                    .findAny();
                        }
                );
        Mockito.when(bookingRepository.save(any(BookingEntity.class)))
                .thenAnswer(invocation -> {
                            BookingEntity ent = invocation.getArgument(0);
                            if (ent.getId() == null || ent.getId() == 0) ent.setId(1);
                            return ent;
                        }
                );
        Mockito.when(userRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    int id = invocation.getArgument(0);
                    return ofNullable(
                            id == expectedUserId ? expectedOwner :
                                    id == expectedBookerId ? expectedBooker :
                                            null
                    ).map(dto -> new UserEntity(
                            dto.getId(),
                            dto.getName(),
                            dto.getEmail()
                    ));
                });
        Mockito.when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    int id = invocation.getArgument(0);
                    return ofNullable(
                            id == expectedItem.getId() ? expectedItem :
                                    id == expectedLockedItem.getId() ? expectedLockedItem :
                                            null
                    ).map(dto -> new ItemEntity(
                            dto.getId(),
                            dto.getName(),
                            dto.getDescription(),
                            dto.getAvailable(),
                            expectedUserEntity,
                            Set.of(),
                            null
                    ));
                });
        Mockito.when(bookingMapper.entityToBookingDtoRead(any(BookingEntity.class)))
                .thenAnswer(invocation -> {
                            BookingEntity ent = invocation.getArgument(0);
                            return new BookingDtoRead(
                                   ent.getId(),
                                   ent.getStartDateTime(),
                                   ent.getEndDateTime(),
                                   expectedItem,
                                   expectedBooker,
                                    ent.getStatus()
                            );
                        }
                );
        Mockito.when(bookingMapper.bookingDtoAddToEntity(any(BookingDtoAdd.class)))
                .thenAnswer(invocation -> {
                            BookingDtoAdd dto = invocation.getArgument(0);
                            return new BookingEntity(
                                    1,
                                    dto.getStart(),
                                    dto.getEnd(),
                                    expectedItemEntity,
                                    expectedBookerEntity,
                                    Status.WAITING
                            );
                        }
                );

        String stateInvalid = "invalid";
        String stateNone = null;
        String stateCurrent = "CURRENT";

        //////////////////
        // getAllUserBookings
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllUserBookings(expectedBookerId, stateInvalid, null, null)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllUserBookings(expectedBookerId, stateNone, null, null)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllUserBookings(expectedBookerId, stateCurrent, -1, 1)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllUserBookings(expectedBookerId, stateCurrent, 0, 0)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllUserBookings(expectedBookerId, stateCurrent, 0, -12)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllUserBookings(expectedBookerId, stateCurrent, -1, -12)
        );

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.getAllUserBookings(-1, stateCurrent, 1, 1)
        );

        List<BookingDtoRead> actualList = bookingService.getAllUserBookings(expectedBookerId, stateCurrent, null, null);
        assertThat(actualList)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .extracting(BookingDtoRead::getId)
                .isEqualTo(ofNullable(expectedBookings.getOrDefault(stateCurrent, null)).map(BookingEntity::getId).orElse(-999));

        /////////////////////////////
        // getAllOwner

        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllOwner(expectedUserId, stateInvalid, null, null)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllOwner(expectedUserId, stateNone, null, null)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllOwner(expectedUserId, stateCurrent, -1, 1)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllOwner(expectedUserId, stateCurrent, 0, 0)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllOwner(expectedUserId, stateCurrent, 0, -12)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.getAllOwner(expectedUserId, stateCurrent, -1, -12)
        );

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.getAllOwner(-1, stateCurrent, 1, 1)
        );

        actualList = bookingService.getAllOwner(expectedUserId, stateCurrent, null, null);
        assertThat(actualList)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .extracting(BookingDtoRead::getId)
                .isEqualTo(ofNullable(expectedBookings.getOrDefault(stateCurrent, null)).map(BookingEntity::getId).orElse(-999));

        //////////////////////
        // getSpecificBooking

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.getSpecificBooking(-1, 1)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.getSpecificBooking(expectedUserId, -1)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.getSpecificBooking(expectedBookerId, -1)
        );
        int checkIndex = 5;
        BookingDtoRead actual = bookingService.getSpecificBooking(expectedUserId, checkIndex);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expectedBookings.values().stream()
                        .filter(ent -> ent.getId() == checkIndex)
                        .findAny()
                        .map(bookingMapper::entityToBookingDtoRead)
                        .orElse(null));
        int checkIndex2 = 4;
        actual = bookingService.getSpecificBooking(expectedBookerId, checkIndex2);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expectedBookings.values().stream()
                        .filter(ent -> ent.getId() == checkIndex2)
                        .findAny()
                        .map(bookingMapper::entityToBookingDtoRead)
                        .orElse(null));

        //////////////////////
        // addBooking
        BookingDtoAdd bookingDtoAddInvalid1 = new BookingDtoAdd(null,           now().plusMinutes(10), now().plusHours(1));
        BookingDtoAdd bookingDtoAddInvalid2 = new BookingDtoAdd(expectedItem.getId(), null,           now().plusHours(1));
        BookingDtoAdd bookingDtoAddInvalid3 = new BookingDtoAdd(expectedItem.getId(), now().minusHours(1), null);
        BookingDtoAdd bookingDtoAddInvalid4 = new BookingDtoAdd(expectedItem.getId(), now().plusMinutes(10).truncatedTo(ChronoUnit.MINUTES), now().plusMinutes(10).truncatedTo(ChronoUnit.MINUTES));
        BookingDtoAdd bookingDtoAddInvalid5 = new BookingDtoAdd(expectedItem.getId(), now().plusMinutes(100), now().plusMinutes(10));
        BookingDtoAdd bookingDtoAddInvalid6 = new BookingDtoAdd(999,           now().plusMinutes(10), now().plusHours(1));
        BookingDtoAdd bookingDtoAddInvalid7 = new BookingDtoAdd(expectedLockedItem.getId(),           now().plusMinutes(10), now().plusHours(1));
        BookingDtoAdd bookingDtoAdd =         new BookingDtoAdd(expectedItem.getId(), now().plusMinutes(10), now().plusHours(10));

        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid1, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid2, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid3, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid4, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid5, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid6, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.addBooking(bookingDtoAddInvalid7, expectedBookerId)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.addBooking(bookingDtoAdd, -999)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.addBooking(bookingDtoAdd, expectedUserId)
        );
        BookingDtoRead actual2 = bookingService.addBooking(bookingDtoAdd, expectedBookerId);
        assertThat(actual2)
                .isNotNull();
        //////////////////////
        // updateBookingStatus

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(-99, -99, true)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(1, -99, true)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(-99, expectedUserId, true)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(-99, expectedUserId, false)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(-99, expectedBookerId, true)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(-99, expectedBookerId, false)
        );
        int checkIndex3 = 3;
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(checkIndex3, expectedBookerId, true)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> bookingService.updateBookingStatus(checkIndex3, expectedBookerId, false)
        );
        BookingDtoRead actualNewBooking = bookingService.updateBookingStatus(checkIndex3, expectedUserId, true);
        assertThat(actualNewBooking)
                .isNotNull()
                .extracting(BookingDtoRead::getStatus)
                .isEqualTo(Status.APPROVED);
        int checkIndex4 = 4;
        actualNewBooking = bookingService.updateBookingStatus(checkIndex4, expectedUserId, false);
        assertThat(actualNewBooking)
                .isNotNull()
                .extracting(BookingDtoRead::getStatus)
                .isEqualTo(Status.REJECTED);

        // повторно
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> bookingService.updateBookingStatus(checkIndex4, expectedUserId, false)
        );
    }
}