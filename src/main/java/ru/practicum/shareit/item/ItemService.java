package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Item Service
 */
@Service
public class ItemService {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemMapper mapper;

    public List<ItemDtoRead> getAllItem(Integer userId) {
        return mapper.entityToItemDtoReadList(repository.findByOwnerId(userId));
    }

    public List<ItemDtoRead> findBySubstr(String namePart) {
        return StringUtils.isBlank(namePart)
                ? new ArrayList<>()
                : mapper.entityToItemDtoReadList(
                repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(namePart, namePart)
                        .stream()
                        .filter(ItemEntity::getAvailable)
                        .collect(Collectors.toList()));
    }

    public ItemDtoRead getByIdItem(Integer itemId, Integer userId) {
        return repository.findById(itemId)
                .map(mapper::entityToItemDtoRead)
                .orElseThrow(() -> new BadRequestException("Такой вещи нет"));
    }

    public ItemDtoRead addItem(ItemDtoAdd itemDtoAdd, Integer userId) {
        Optional.ofNullable(itemDtoAdd.getAvailable())
                .orElseThrow(() -> new BadRequestException("Укажите статус доступности вещи"));
        if (StringUtils.isBlank(itemDtoAdd.getName()) || StringUtils.isEmpty(itemDtoAdd.getDescription())) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        ItemEntity itemEntity = mapper.itemDtoAddToEntityItem(itemDtoAdd);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        itemEntity.setOwner(userEntity);
        return mapper.entityToItemDtoRead(repository.save(itemEntity));
    }

    public ItemDtoRead updateItem(ItemDtoUpdate itemDtoUpdate, Integer userId, Integer itemId) {
        ItemEntity item = repository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Такой вещи нет"));
        Optional.ofNullable(item.getOwner())
                .map(UserEntity::getId)
                .filter(userId::equals)
                .orElseThrow(() -> new ResourceNotFoundException("Этот пользователь не является владельцем вещи"));
        Optional.ofNullable(itemDtoUpdate.getName()).map(value -> {
            item.setName(value);
            return item;
        }).ifPresent(repository::save);
        Optional.ofNullable(itemDtoUpdate.getDescription()).map(value -> {
            item.setDescription(value);
            return item;
        }).ifPresent(repository::save);
        Optional.ofNullable(itemDtoUpdate.getAvailable()).map(value -> {
            item.setAvailable(value);
            return item;
        }).ifPresent(repository::save);
        return mapper.entityToItemDtoRead(repository.save(item));
    }
}
