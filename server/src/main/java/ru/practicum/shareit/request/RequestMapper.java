package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface RequestMapper {

    @Mapping(target = "authorId", source = "author.id")
    RequestDtoRead entityToRequestDtoRead(RequestEntity value);

    RequestEntity requestDtoAddToEntity(RequestDtoAdd value);

}
