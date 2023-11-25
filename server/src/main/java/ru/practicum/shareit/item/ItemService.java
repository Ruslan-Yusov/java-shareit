package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * Item Service
 */
@Service
public class ItemService {

    public static final String MESSAGE_NO_ITEM_FOUND = "Такой вещи нет";
    @Autowired
    private ItemRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private RequestRepository requestRepository;

    public List<ItemDtoRead> getAllItem(Integer userId) {
       return repository.findByOwnerId(userId).stream()
                .map(mapper::entityToItemDtoRead)
                .map(dto -> addLastBookingToDtoForOwner(dto.getId(), userId, dto))
                .sorted(Comparator.comparing(ItemDtoRead::getId))
                .collect(Collectors.toList());
    }

    public List<ItemDtoRead> findBySubstr(String namePart) {
        return StringUtils.isBlank(namePart)
                ? new ArrayList<>()
                :
                repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(namePart, namePart)
                        .stream()
                        .filter(ItemEntity::getAvailable)
                        .map(mapper::entityToItemDtoRead)
                        .collect(Collectors.toList());
    }

    public ItemDtoRead getByIdItem(Integer itemId, Integer userId) {
        return repository.findById(itemId)
                .map(mapper::entityToItemDtoRead)
                .map(dto -> addLastBookingToDtoForOwner(itemId, userId, dto))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_ITEM_FOUND));
    }

    @NotNull
    public ItemDtoRead addLastBookingToDtoForOwner(Integer itemId, Integer userId, ItemDtoRead dto) {
        if (dto.getOwner().getId() == userId) {
            List<BookingEntity> bookingsByItemId = bookingRepository.findBookingsByItemId(itemId);
            if (!bookingsByItemId.isEmpty()) {
                BookingEntity booking1 = bookingsByItemId.get(0);
                BookingEntity booking2 = (bookingsByItemId.size() > 1) ? bookingsByItemId.get(1) : null;
                if (booking1.getStartDateTime().isAfter(LocalDateTime.now())) {
                    dto.setNextBooking(bookingMapper.entityToBookingDtoReadSimple(booking1));
                    dto.setLastBooking(bookingMapper.entityToBookingDtoReadSimple(booking2));
                } else {
                    dto.setLastBooking(bookingMapper.entityToBookingDtoReadSimple(booking1));
                    dto.setNextBooking(bookingMapper.entityToBookingDtoReadSimple(booking2));
                }
            }
        }
        return dto;
    }

    public ItemDtoRead addItem(ItemDtoAdd itemDtoAdd, Integer userId) {
        ofNullable(itemDtoAdd.getAvailable())
                .orElseThrow(() -> new BadRequestException("Укажите статус доступности вещи"));
        if (StringUtils.isBlank(itemDtoAdd.getName()) || StringUtils.isEmpty(itemDtoAdd.getDescription())) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        ItemEntity itemEntity = mapper.itemDtoAddToEntityItem(itemDtoAdd);
        itemEntity.setOwner(userEntity);
        ofNullable(itemDtoAdd.getRequestId())
                .flatMap(r -> requestRepository.findById(r))
                .ifPresent(itemEntity::setRequest);
        return mapper.entityToItemDtoRead(repository.save(itemEntity));
    }

    public ItemDtoRead updateItem(ItemDtoUpdate itemDtoUpdate, Integer userId, Integer itemId) {
        ItemEntity item = repository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_ITEM_FOUND));
        ofNullable(item.getOwner())
                .map(UserEntity::getId)
                .filter(userId::equals)
                .orElseThrow(() -> new ResourceNotFoundException("Этот пользователь не является владельцем вещи"));
        ofNullable(itemDtoUpdate.getName()).map(value -> {
            item.setName(value);
            return item;
        }).ifPresent(repository::save);
        ofNullable(itemDtoUpdate.getDescription()).map(value -> {
            item.setDescription(value);
            return item;
        }).ifPresent(repository::save);
        ofNullable(itemDtoUpdate.getAvailable()).map(value -> {
            item.setAvailable(value);
            return item;
        }).ifPresent(repository::save);
        return mapper.entityToItemDtoRead(repository.save(item));
    }

    public CommentDtoRead addItemComment(Integer userId, Integer itemId, CommentDtoAdd commentDtoAdd) {
        ItemEntity item = repository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_ITEM_FOUND));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        if (0 == bookingRepository.countBookingByItemIdAndBookerId(itemId, userId)) {
            throw new BadRequestException("Вы не бронировали эту вещь");
        }
        if (StringUtils.isBlank(commentDtoAdd.getText())) {
            throw new BadRequestException("Не задан текст");
        }
        CommentEntity commentEntity = new CommentEntity(
                null,
                commentDtoAdd.getText(),
                item,
                userEntity,
                LocalDateTime.now());
        Set<CommentEntity> comment = item.getComments();
        comment.add(commentEntity);
        item.setComments(comment);
        item = repository.save(item);
        return item.getComments().stream()
                .max(Comparator.comparing(CommentEntity::getCreated))
                .map(mapper::entityToCommentDtoRead)
                .orElseThrow();
    }
}
