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
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoRead;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestService requestService;

    @Test
    void testRequestService() {
        int expectedUserId = 1;
        int expectedBookerId = 2;
        LocalDateTime expectedTime = LocalDateTime.now();
        UserDtoRead expectedOwner = new UserDtoRead(expectedUserId,
                "name",
                "email@mail.ru");
        UserDtoRead expectedBooker = new UserDtoRead(expectedBookerId,
                "name2",
                "email2@mail.ru");
        UserEntity expectedUserEntity = new UserEntity(
                expectedOwner.getId(),
                expectedOwner.getName(),
                expectedOwner.getEmail());
        UserEntity expectedBookerEntity = new UserEntity(
                expectedBooker.getId(),
                expectedBooker.getName(),
                expectedBooker.getEmail());

        ItemDtoRead expectedItem = new ItemDtoRead(1,
                "item",
                expectedOwner,
                "description",
                true,
                null,
                null,
                new HashSet<>(),
                1);
        ItemEntity expectedItemEntity = new ItemEntity(
                expectedItem.getId(),
                expectedItem.getName(),
                expectedItem.getDescription(),
                expectedItem.getAvailable(),
                expectedUserEntity,
                new HashSet<>(),
                null
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
        Mockito.when(requestRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    int id = invocation.getArgument(0);
                    return ofNullable(id < 0 ? null :
                            new RequestEntity(
                                    id,
                                    String.format("description-%d", id),
                                    expectedBookerEntity,
                                    expectedTime,
                                    Set.of()
                            ));
                });
        Mockito.when(requestRepository.findAll())
                .thenAnswer(dummy -> List.of(
                        requestRepository.findById(1).get(),
                        requestRepository.findById(2).get()
                ));
        Mockito.when(requestRepository.save(any(RequestEntity.class)))
                .thenAnswer(invocation -> {
                    RequestEntity ent = invocation.getArgument(0);
                            if (ent.getId() == null || ent.getId() == 0) ent.setId(1);
                            return ent;
                        }
                );
        Mockito.when(requestMapper.entityToRequestDtoRead(any(RequestEntity.class)))
                .thenAnswer(invocation -> {
                    RequestEntity entity = invocation.getArgument(0);
                    return new RequestDtoRead(
                            entity.getId(),
                            entity.getDescription(),
                            expectedBookerId,
                            expectedTime,
                            Set.of(expectedItem)
                    );
                });
        Mockito.when(requestMapper.requestDtoAddToEntity(any(RequestDtoAdd.class)))
                .thenAnswer(invocation -> {
                    RequestDtoAdd dto = invocation.getArgument(0);
                    return new RequestEntity(
                            1,
                            dto.getDescription(),
                            expectedBookerEntity,
                            expectedTime,
                            Set.of(expectedItemEntity)
                    );
                });
        ////////////////////
        // getAllRequest

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> requestService.getAllRequest(-99)
        );
        List<RequestDtoRead> actualList = requestService.getAllRequest(expectedBookerId);
        assertThat(actualList)
                .isNotNull()
                .hasSize(2);

        ////////////////////
        // getById

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> requestService.getById(1,-99)
        );
        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> requestService.getById(-99, expectedBookerId)
        );
        RequestDtoRead actual = requestService.getById(1, expectedBookerId);
        assertThat(actual)
                .isNotNull();

        ////////////////////
        // getAllRequestPag

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> requestService.getAllRequestPag(-99, null, null)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> requestService.getAllRequestPag(expectedBookerId, -99, null)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> requestService.getAllRequestPag(expectedBookerId, 1, -99)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> requestService.getAllRequestPag(expectedBookerId, 1, 0)
        );
        actualList = requestService.getAllRequestPag(expectedBookerId, null, null);
        assertThat(actualList)
                .isNotNull()
                .hasSize(2);
        actualList = requestService.getAllRequestPag(expectedBookerId, 0, 999);
        assertThat(actualList)
                .isNotNull()
                .hasSize(2);

        ////////////////////
        // addRequest
        RequestDtoAdd requestDtoAddInvalid1 =  new RequestDtoAdd(null, expectedBookerId);
        RequestDtoAdd requestDtoAddInvalid2 =  new RequestDtoAdd(" ", expectedBookerId);
        RequestDtoAdd requestDtoAdd1 =  new RequestDtoAdd("request-dsc", expectedBookerId);

        Assertions.assertThrowsExactly(
                ResourceNotFoundException.class,
                () -> requestService.addRequest(-99, requestDtoAdd1)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> requestService.addRequest(expectedBookerId, requestDtoAddInvalid1)
        );
        Assertions.assertThrowsExactly(
                BadRequestException.class,
                () -> requestService.addRequest(expectedBookerId, requestDtoAddInvalid2)
        );
        RequestDtoRead actual1 = requestService.addRequest(expectedBookerId, requestDtoAdd1);
        assertThat(actual1)
                .isNotNull();

    }

}