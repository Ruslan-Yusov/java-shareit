package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.BadRequestException;
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

    public static final String MESSAGE_NO_USER_FOUND = "Такого пользователя нет";
    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserRepository repository;

    private static final String EMAIL_REGEXP_PATTERN = "([\\w\\.\\-]*)@([\\w\\-]*)\\.(\\p{Lower}{2,4})";
    private static final Pattern EMAIL_REGEXP = Pattern.compile(EMAIL_REGEXP_PATTERN);

    public List<UserDtoRead> getAllUsers() {
        return mapper.entityToUserDtoForRedList(repository.findAll());
    }

    public UserDtoRead getUserDto(Integer id) {
        return repository.findById(id)
                .map(mapper::entityToUserDtoForRead)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NO_USER_FOUND));
    }

    public UserDtoRead addUser(UserDtoAdd userDtoAdd) {
        UserEntity userEntity = mapper.userDtoAddToUserEntity(userDtoAdd);
          repository.save(userEntity);
        return mapper.entityToUserDtoForRead(userEntity);
    }

    public UserDtoRead updateUser(UserDtoUpdate userDtoUpdate, Integer id) {
        UserEntity user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        if (Objects.nonNull(userDtoUpdate.getEmail())
                && !userDtoUpdate.getEmail().equals(user.getEmail())
                && !EMAIL_REGEXP.matcher(userDtoUpdate.getEmail()).matches()) {
            throw new BadRequestException("Email не валидный");
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
