package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDtoAdd;
import ru.practicum.shareit.user.dto.UserDtoRead;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserDtoRead> entityToUserDtoForRedList(Collection<UserEntity> value);

    UserDtoRead entityToUserDtoForRead(UserEntity value);

    UserEntity userDtoReadToUserEntity(UserDtoRead value);

    UserEntity userDtoAddToUserEntity(UserDtoAdd value);
}
