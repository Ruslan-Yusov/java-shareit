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
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.LastBookingDtoRead;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.RequestEntity;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoRead;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemController itemController;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getAllItemTest() {
        Integer userId = 1;
        ItemDtoRead expected = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "item description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        Mockito
                .when(itemRepository.findByOwnerId(any(Integer.class)))
                .thenAnswer(invocation -> Stream.of(
                                new UserEntity(
                                        invocation.getArgument(0),
                                        expected.getOwner().getName(),
                                        expected.getOwner().getEmail()))
                        .map(user ->
                                new ItemEntity(
                                        expected.getId(),
                                        expected.getName(),
                                        expected.getDescription(),
                                        expected.getAvailable(),
                                        user,
                                        new HashSet<>(),
                                        new RequestEntity() { { setId(expected.getRequestId());
                                            }
                                        }
                                ))
                        .collect(Collectors.toList())
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );

        List<ItemDtoRead> actual = itemService.getAllItem(userId);
        assertThat(actual)
                .hasSize(1)
                .element(0)
                .satisfies(dto -> assertThat(dto).isEqualTo(expected));
    }

    @Test
    void getAllItemWithBookingsTest() {
        int userId = 1;
        int bookerId = 2;
        ItemDtoRead expected = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "item description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        Mockito.when(itemRepository.findByOwnerId(any(Integer.class)))
                .thenAnswer(invocation -> Stream.of(
                                new UserEntity(
                                        invocation.getArgument(0),
                                        expected.getOwner().getName(),
                                        expected.getOwner().getEmail()))
                        .map(user ->
                                new ItemEntity(
                                        expected.getId(),
                                        expected.getName(),
                                        expected.getDescription(),
                                        expected.getAvailable(),
                                        user,
                                        new HashSet<>(),
                                        new RequestEntity() { { setId(expected.getRequestId());
                                            }
                                        }
                                ))
                        .collect(Collectors.toList())
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );
        BookingEntity bookingFirst = new BookingEntity(
                1,
                LocalDateTime.now().minusHours(10),
                LocalDateTime.now().minusHours(8),
                null /*item*/,
                null /* user */,
                Status.APPROVED
        );
        BookingEntity bookingLast = new BookingEntity(
                2,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                null /*item*/,
                null /* user */,
                Status.APPROVED
        );
        Mockito.when(bookingRepository.findBookingsByItemId(anyInt()))
                .thenAnswer(invocation -> List.of(bookingLast, bookingFirst));
        Mockito.when(bookingMapper.entityToBookingDtoReadSimple(any(BookingEntity.class)))
                .thenAnswer(invocation -> {
                    BookingEntity booking = invocation.getArgument(0);
                    return new LastBookingDtoRead(
                            booking.getId(),
                            booking.getStartDateTime(),
                            booking.getEndDateTime(),
                            bookerId,
                            booking.getStatus()
                    );
                });

        LastBookingDtoRead bookingLastExpected = new LastBookingDtoRead(
                bookingFirst.getId(),
                bookingFirst.getStartDateTime(),
                bookingFirst.getEndDateTime(),
                bookerId,
                bookingFirst.getStatus()
        );
        LastBookingDtoRead bookingNextExpected = new LastBookingDtoRead(
                bookingLast.getId(),
                bookingLast.getStartDateTime(),
                bookingLast.getEndDateTime(),
                bookerId,
                bookingLast.getStatus()
        );
        List<ItemDtoRead> actual = itemService.getAllItem(userId);
        assertThat(actual)
                .hasSize(1)
                .element(0)
                .satisfies(dto -> assertThat(dto.getLastBooking()).isEqualTo(bookingLastExpected))
                .satisfies(dto -> assertThat(dto.getNextBooking()).isEqualTo(bookingNextExpected));
    }

    @Test
    void getAllItemWithBookingsTestReversed() {
        Integer userId = 1;
        Integer bookerId = 2;
        ItemDtoRead expected = new ItemDtoRead(1,
                "item",
                new UserDtoRead(1,
                        "name",
                        "email@mail.ru"),
                "item description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        Mockito.when(itemRepository.findByOwnerId(any(Integer.class)))
                .thenAnswer(invocation -> Stream.of(
                                new UserEntity(
                                        invocation.getArgument(0),
                                        expected.getOwner().getName(),
                                        expected.getOwner().getEmail()))
                        .map(user ->
                                new ItemEntity(
                                        expected.getId(),
                                        expected.getName(),
                                        expected.getDescription(),
                                        expected.getAvailable(),
                                        user,
                                        new HashSet<>(),
                                        new RequestEntity() { { setId(expected.getRequestId());
                                            }
                                        }
                                ))
                        .collect(Collectors.toList())
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );
        BookingEntity bookingFirst = new BookingEntity(
                1,
                LocalDateTime.now().minusHours(10),
                LocalDateTime.now().minusHours(8),
                null /*item*/,
                null /* user */,
                Status.APPROVED
        );
        BookingEntity bookingLast = new BookingEntity(
                2,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                null /*item*/,
                null /* user */,
                Status.APPROVED
        );
        Mockito.when(bookingRepository.findBookingsByItemId(anyInt()))
                .thenAnswer(invocation -> List.of(bookingFirst, bookingLast));
        Mockito.when(bookingMapper.entityToBookingDtoReadSimple(any(BookingEntity.class)))
                .thenAnswer(invocation -> {
                    BookingEntity booking = invocation.getArgument(0);
                    return new LastBookingDtoRead(
                            booking.getId(),
                            booking.getStartDateTime(),
                            booking.getEndDateTime(),
                            bookerId,
                            booking.getStatus()
                    );
                });

        LastBookingDtoRead bookingLastExpected = new LastBookingDtoRead(
                bookingFirst.getId(),
                bookingFirst.getStartDateTime(),
                bookingFirst.getEndDateTime(),
                bookerId,
                bookingFirst.getStatus()
        );
        LastBookingDtoRead bookingNextExpected = new LastBookingDtoRead(
                bookingLast.getId(),
                bookingLast.getStartDateTime(),
                bookingLast.getEndDateTime(),
                bookerId,
                bookingLast.getStatus()
        );
        List<ItemDtoRead> actual = itemService.getAllItem(userId);
        assertThat(actual)
                .hasSize(1)
                .element(0)
                .satisfies(dto -> assertThat(dto.getLastBooking()).isEqualTo(bookingLastExpected))
                .satisfies(dto -> assertThat(dto.getNextBooking()).isEqualTo(bookingNextExpected));
    }

    @Test
    void findBySubstr() {
        assertThat(itemService.findBySubstr(""))
                .isNotNull()
                .isEmpty();

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
        Mockito.when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(anyString(), anyString()))
                .thenAnswer(invocation ->
                        Stream.of(
                                        new UserEntity(
                                                expected.getOwner().getId(),
                                                expected.getOwner().getName(),
                                                expected.getOwner().getEmail())
                                ).map(user ->
                                        new ItemEntity(
                                                expected.getId(),
                                                expected.getName(),
                                                expected.getDescription(),
                                                expected.getAvailable(),
                                                user,
                                                new HashSet<>(),
                                                new RequestEntity() { { setId(expected.getRequestId());
                                                    }
                                                }
                                        ))
                                .collect(Collectors.toList())
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );

        List<ItemDtoRead> actual = itemService.findBySubstr("fake");
        assertThat(actual)
                .isNotNull()
                .hasSize(1)
                .element(0)
                .satisfies(item -> assertThat(item).isEqualTo(expected));
    }

    @Test
    void getByIdItem() {
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
        Mockito.when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                            if ((int) invocation.getArgument(0) > 0) {
                                return Optional.of(
                                        new ItemEntity(
                                                expected.getId(),
                                                expected.getName(),
                                                expected.getDescription(),
                                                expected.getAvailable(),
                                                new UserEntity(
                                                        invocation.getArgument(0),
                                                        expected.getOwner().getName(),
                                                        expected.getOwner().getEmail()),
                                                new HashSet<>(),
                                                new RequestEntity() { { setId(expected.getRequestId());
                                                    }
                                                }
                                        ));
                            } else {
                                return Optional.empty();
                            }
                        }
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> itemService.getByIdItem(-999, -999)
        );

        ItemDtoRead actual = itemService.getByIdItem(1, 1);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void addItem() {
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
        Mockito.when(userRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    if ((int) invocation.getArgument(0) > 0) {
                        return Optional.of(
                                new UserEntity(
                                        invocation.getArgument(0),
                                        expected.getOwner().getName(),
                                        expected.getOwner().getEmail()));
                    } else {
                        return Optional.empty();
                    }
                });
        Mockito.when(itemRepository.save(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                    ItemEntity itemEntity = invocation.getArgument(0);
                    Integer oldId = itemEntity.getId();
                    itemEntity.setId(oldId == null || oldId == 0 ? 1 : oldId);
                    return itemEntity;
                });
        Mockito.when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation ->
                        Optional.of(
                                new ItemEntity(
                                        expected.getId(),
                                        expected.getName(),
                                        expected.getDescription(),
                                        expected.getAvailable(),
                                        new UserEntity(
                                                invocation.getArgument(0),
                                                expected.getOwner().getName(),
                                                expected.getOwner().getEmail()),
                                        new HashSet<>(),
                                        new RequestEntity() { { setId(expected.getRequestId());
                                            }
                                        }
                                ))
                );
        Mockito.when(itemMapper.itemDtoAddToEntityItem(any(ItemDtoAdd.class)))
                .thenAnswer(invocation -> {
                            ItemDtoAdd dto = invocation.getArgument(0);
                            return new ItemEntity(
                                    0,
                                    dto.getName(),
                                    dto.getDescription(),
                                    dto.getAvailable(),
                                    new UserEntity(
                                            expected.getOwner().getId(),
                                            expected.getOwner().getName(),
                                            expected.getOwner().getEmail()),
                                    new HashSet<>(),
                                    new RequestEntity() { { setId(expected.getRequestId());
                                        }
                                    }
                            );
                        }
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );


        ItemDtoAdd itemDtoAddInvalid1 = new ItemDtoAdd("item", "dsc", null, 1, null);
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> itemService.addItem(itemDtoAddInvalid1, 1)
        );
        ItemDtoAdd itemDtoAddInvalid2 = new ItemDtoAdd("  ", "dsc", true, 1, null);
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> itemService.addItem(itemDtoAddInvalid2, 1)
        );
        ItemDtoAdd itemDtoAddInvalid3 = new ItemDtoAdd("name", null, true, 1, null);
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> itemService.addItem(itemDtoAddInvalid3, 1)
        );
        ItemDtoAdd itemDtoAdd = new ItemDtoAdd(
                expected.getName(), expected.getDescription(),
                expected.getAvailable(), expected.getOwner().getId(), null);
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> itemService.addItem(itemDtoAdd, -999)
        );
        ItemDtoRead actual = itemService.addItem(itemDtoAdd, 1);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void updateItem() {
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
        Mockito.when(itemRepository.save(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                    ItemEntity itemEntity = invocation.getArgument(0);
                    Integer oldId = itemEntity.getId();
                    itemEntity.setId(oldId == null ? 1 : oldId);
                    return itemEntity;
                });
        Mockito.when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                            Integer id = invocation.getArgument(0);
                            if (id > 0) {
                                return Optional.of(
                                        new ItemEntity(
                                                expected.getId(),
                                                expected.getName(),
                                                expected.getDescription(),
                                                expected.getAvailable(),
                                                new UserEntity(
                                                        id,
                                                        expected.getOwner().getName(),
                                                        expected.getOwner().getEmail()),
                                                new HashSet<>(),
                                                new RequestEntity() { { setId(expected.getRequestId());
                                                    }
                                                }
                                        ));
                            } else {
                                return Optional.empty();
                            }
                        }
                );
        Mockito.when(itemMapper.entityToItemDtoRead(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                            ItemEntity item = invocation.getArgument(0);
                            return new ItemDtoRead(
                                    item.getId(),
                                    item.getName(),
                                    ofNullable(item.getOwner())
                                            .map(ow -> new UserDtoRead(
                                                    ow.getId(),
                                                    ow.getName(),
                                                    ow.getEmail()
                                            ))
                                            .orElseThrow(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    null,
                                    null,
                                    item.getComments().stream()
                                            .map(cm -> new CommentDtoRead(
                                                    cm.getId(),
                                                    cm.getText(),
                                                    cm.getAuthor().getName(),
                                                    cm.getCreated()
                                            ))
                                            .collect(Collectors.toSet()),
                                    ofNullable(item.getRequest())
                                            .map(RequestEntity::getId)
                                            .orElse(null)
                            );
                        }
                );


        ItemDtoUpdate itemDtoUpdate1 = new ItemDtoUpdate("itemnew", "dscnew", false);
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> itemService.updateItem(itemDtoUpdate1, -999, -1)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> itemService.updateItem(itemDtoUpdate1, 1, -999)
        );
        ItemDtoRead actual = itemService.updateItem(itemDtoUpdate1, 1, 1);
        expected.setName(itemDtoUpdate1.getName());
        expected.setDescription(itemDtoUpdate1.getDescription());
        expected.setAvailable(itemDtoUpdate1.getAvailable());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void addItemComment() {

        UserDtoRead author =
                new UserDtoRead(2,
                        "name",
                        "email@mail.ru");
        ItemDtoRead expectedItem = new ItemDtoRead(1,
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

        String someCommentText = "some comment";
        LocalDateTime commentDate = LocalDateTime.now();
        Mockito.when(userRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    int id = invocation.getArgument(0);
                    if (id == 2) {
                        return Optional.of(
                                new UserEntity(
                                        invocation.getArgument(0),
                                        author.getName(),
                                        author.getEmail()));
                    } else if (id > 0) {
                        return Optional.of(
                                new UserEntity(
                                        invocation.getArgument(0),
                                        expectedItem.getOwner().getName(),
                                        expectedItem.getOwner().getEmail()));
                    } else {
                        return Optional.empty();
                    }
                });
        Mockito.when(itemRepository.save(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                    ItemEntity itemEntity = invocation.getArgument(0);
                    Integer oldId = itemEntity.getId();
                    itemEntity.setId(oldId == null ? 1 : oldId);
                    Set<CommentEntity> comments = itemEntity.getComments().stream()
                            .peek(comment -> {
                                if (comment.getId() == null) comment.setId(1);
                                comment.setCreated(commentDate);
                            }).collect(Collectors.toSet());
                    itemEntity.setComments(comments);
                    return itemEntity;
                });
        Mockito.when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                            Integer id = invocation.getArgument(0);
                            if (id > 0) {
                                return Optional.of(
                                        new ItemEntity(
                                                expectedItem.getId(),
                                                expectedItem.getName(),
                                                expectedItem.getDescription(),
                                                expectedItem.getAvailable(),
                                                new UserEntity(
                                                        id,
                                                        expectedItem.getOwner().getName(),
                                                        expectedItem.getOwner().getEmail()),
                                                new HashSet<>(),
                                                new RequestEntity() { { setId(expectedItem.getRequestId());
                                                    }
                                                }
                                        ));
                            } else {
                                return Optional.empty();
                            }
                        }
                );
        Mockito.when(bookingRepository.countBookingByItemIdAndBookerId(anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    int itemId = invocation.getArgument(0);
                    int userId = invocation.getArgument(1);
                    if (userId != 2 || itemId != 1) {
                        return 0;
                    } else {
                        return 1;
                    }
                });
        Mockito.when(itemMapper.entityToCommentDtoRead(any(CommentEntity.class)))
                .thenAnswer(invocation -> {
                    CommentEntity commentEntity = invocation.getArgument(0);
                    return new CommentDtoRead(
                            commentEntity.getId(),
                            commentEntity.getText(),
                            commentEntity.getAuthor().getName(),
                            commentEntity.getCreated());
                });

        CommentDtoAdd commentDtoAddInvalid = new CommentDtoAdd("  ");
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> itemService.addItemComment(2, 1, commentDtoAddInvalid)
        );

        CommentDtoAdd commentDtoAdd = new CommentDtoAdd(someCommentText);

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> itemService.addItemComment(1, -999, commentDtoAdd)
        );

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> itemService.addItemComment(-999, 1, commentDtoAdd)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> itemService.addItemComment(4, 1, commentDtoAdd)
        );
        CommentDtoRead expected = new CommentDtoRead(1, commentDtoAdd.getText(), author.getName(), commentDate);
        CommentDtoRead actual = itemService.addItemComment(2, 1, commentDtoAdd);
        assertThat(actual).isNotNull().isEqualTo(expected);
        // test entity method
        final Set<CommentEntity> commentCache = new HashSet<>();
        // modify mocks to store comments
        Mockito.when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                            Integer id = invocation.getArgument(0);
                            if (id > 0) {
                                return Optional.of(
                                        new ItemEntity(
                                                expectedItem.getId(),
                                                expectedItem.getName(),
                                                expectedItem.getDescription(),
                                                expectedItem.getAvailable(),
                                                new UserEntity(
                                                        id,
                                                        expectedItem.getOwner().getName(),
                                                        expectedItem.getOwner().getEmail()),
                                                commentCache,
                                                new RequestEntity() { { setId(expectedItem.getRequestId());
                                                }
                                                }
                                        ));
                            } else {
                                return Optional.empty();
                            }
                        }
                );
        Mockito.when(itemRepository.save(any(ItemEntity.class)))
                .thenAnswer(invocation -> {
                    ItemEntity itemEntity = invocation.getArgument(0);
                    Integer oldId = itemEntity.getId();
                    itemEntity.setId(oldId == null ? 1 : oldId);
                    Set<CommentEntity> comments = itemEntity.getComments().stream()
                            .peek(comment -> {
                                if (comment.getId() == null) comment.setId(1);
                                comment.setCreated(commentDate);
                            }).collect(Collectors.toSet());
                    itemEntity.setComments(comments);
                    commentCache.clear();
                    commentCache.addAll(comments);
                    return itemEntity;
                });
        itemService.addItemComment(2, 1, commentDtoAdd);
        ItemEntity oldItem = itemRepository.findById(1).orElse(null);

        CommentDtoAdd commentDtoAdd2 = new CommentDtoAdd(someCommentText + "2");
        itemService.addItemComment(2, 1, commentDtoAdd2);
        ItemEntity newItem = itemRepository.findById(1).orElse(null);
        CommentEntity fakeComment = new CommentEntity();
        assertThat(newItem)
                .isNotNull()
                .extracting(ItemEntity::getComments)
                .satisfies(list -> assertThat(list.stream().noneMatch(fakeComment::equals)).isTrue())
                // старый комментарий должен остаться
                .satisfies(list -> assertThat(list.stream()
                        .anyMatch(newc -> newItem.getComments().stream().anyMatch(newc::equals)))
                        .isTrue());
        assertThat(oldItem)
                .isNotNull()
                // комментарии не участвуют в сравнении
                .isEqualTo(newItem);
    }


}