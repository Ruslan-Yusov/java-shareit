package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemMapper {
    List<ItemDtoRead> entityToItemDtoReadList(Collection<ItemEntity> value);

    ItemDtoRead entityToItemDtoRead(ItemEntity value);

    ItemEntity itemDtoAddToEntityItem(ItemDtoAdd value);
}

