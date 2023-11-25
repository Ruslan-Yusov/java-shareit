package ru.practicum.shareitgw.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareitgw.connector.UserConnector;
import ru.practicum.shareitgw.exeption.BadRequestException;
import ru.practicum.shareitgw.user.dto.UserDtoAdd;
import ru.practicum.shareitgw.user.dto.UserDtoRead;
import ru.practicum.shareitgw.user.dto.UserDtoUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserGwService {

    @Autowired
    private UserConnector connector;

    private static final String EMAIL_REGEXP_PATTERN = "([\\w\\.\\-]*)@([\\w\\-]*)\\.(\\p{Lower}{2,4})";
    private static final Pattern EMAIL_REGEXP = Pattern.compile(EMAIL_REGEXP_PATTERN);

    public static class UserDtoReadList extends ArrayList<UserDtoRead> {
    }

    public List<UserDtoRead> getAllUsers() {
        return connector.getAll(null);
    }

    public UserDtoRead getUserDto(Integer id) {
        return connector.getById(null, id);
    }

    public UserDtoRead addUser(UserDtoAdd userDtoAdd) {
        if (StringUtils.isBlank(userDtoAdd.getName())) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        if (StringUtils.isBlank(userDtoAdd.getEmail())) {
            throw new BadRequestException("Email не может быть пустым");
        }
        if (!EMAIL_REGEXP.matcher(userDtoAdd.getEmail()).matches()) {
            throw new BadRequestException("Email не валидный");
        }
        return connector.post(null, userDtoAdd);
    }

    public UserDtoRead updateUser(UserDtoUpdate userDtoUpdate, Integer id) {
        return connector.patch(null, userDtoUpdate, id);
    }

    public void deleteUser(Integer id) {
        connector.delete(null, id);
    }
}
