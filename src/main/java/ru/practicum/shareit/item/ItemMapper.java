package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDtoRead;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.user.UserMapper;

import java.util.Set;

/**
 * Item mapper
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemMapper {

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "requestId", source = "request.id")
    ItemDtoRead entityToItemDtoRead(ItemEntity value);

    ItemEntity itemDtoAddToEntityItem(ItemDtoAdd value);

    Set<ItemDtoRead> setEntityToSetItemDtoRead(Set<ItemEntity> value);

    @Mapping(target = "authorName", source = "author.name")
    CommentDtoRead entityToCommentDtoRead(CommentEntity value);

    Set<CommentDtoRead> setEntityToSetCommentDtoRead(Set<CommentEntity> value);

}

