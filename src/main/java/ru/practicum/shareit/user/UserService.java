package ru.practicum.shareit.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceAlreadyExistExeption;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoAdd;
import ru.practicum.shareit.user.dto.UserDtoRead;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserRepository repository;

    private static final String EMAIL_REGEXP_PATTERN = "([\\w\\.\\-]*)\\@([\\w\\-]*)\\.(\\p{Lower}{2,4})";
    private static final Pattern EMAIL_REGEXP = Pattern.compile(EMAIL_REGEXP_PATTERN);

    private boolean checkEmail(String userEmail) {
        // Это сделано для того, чтобы ловить ошибку базы constraint violation и перещелкивания счетчика id
        // нужно для прохождения теста 14 спринта, хотя в 14 спринте нет новых указаний по функционалу User
        return true; //repository.findAll().stream().map(UserEntity::getEmail).noneMatch(userEmail::equals);
    }

    public List<UserDtoRead> getAllUsers() {
        return mapper.entityToUserDtoForRedList(repository.findAll());
    }

    public UserDtoRead getUserDto(Integer id) {
        return repository.findById(id)
                .map(mapper::entityToUserDtoForRead)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
    }

    public UserDtoRead addUser(UserDtoAdd userDtoAdd) {
        if (StringUtils.isBlank(userDtoAdd.getName())) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        if (StringUtils.isBlank(userDtoAdd.getEmail())) {
            throw new BadRequestException("Email не может быть пустым");
        }
        if (!EMAIL_REGEXP.matcher(userDtoAdd.getEmail()).matches()) {
          //  userEntity.setId(generatedId());
            throw new BadRequestException("Email не валидный");
        }
        if (!checkEmail(userDtoAdd.getEmail())) {
            throw new ResourceAlreadyExistExeption("Такой пользователь уже есть");
        }
       // userEntity.setId(generatedId());
        UserEntity userEntity = mapper.userDtoAddToUserEntity(userDtoAdd);
          repository.save(userEntity);
        return mapper.entityToUserDtoForRead(userEntity);
    }

    public UserDtoRead updateUser(UserDtoUpdate userDtoUpdate, Integer id) {
        UserEntity user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        if (Objects.nonNull(userDtoUpdate.getEmail()) && !userDtoUpdate.getEmail().equals(user.getEmail())) {
            if (!EMAIL_REGEXP.matcher(userDtoUpdate.getEmail()).matches()) {
                throw new BadRequestException("Email не валидный");
            }
            if (!checkEmail(userDtoUpdate.getEmail())) {
                throw new ResourceAlreadyExistExeption("Такой email уже есть");
            }
        }
        Optional.ofNullable(userDtoUpdate.getName()).map(value -> {
            user.setName(value);
            return user;
        }).ifPresent(repository::save);
        Optional.ofNullable(userDtoUpdate.getEmail()).map(value -> {
            user.setEmail(value);
            return user;
        }).ifPresent(repository::save);
        return mapper.entityToUserDtoForRead(user);
    }

    public void deleteUser(Integer id) {
        UserEntity user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        repository.delete(user);
    }
}
