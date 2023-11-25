package ru.practicum.shareitgw.connector;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.shareitgw.user.UserGwService;
import ru.practicum.shareitgw.user.dto.UserDtoAdd;
import ru.practicum.shareitgw.user.dto.UserDtoRead;
import ru.practicum.shareitgw.user.dto.UserDtoUpdate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserConnector {
    private final BaseClient client;
    @Value("${shareit-gateway.services.user}")
    private String baseUrl;

    public List<UserDtoRead> getAll(Integer userId) {
        return client.get(baseUrl, userId, UserGwService.UserDtoReadList.class).getBody();
    }

    public UserDtoRead getById(Integer userId, Integer id) {
        return client.get(baseUrl.concat("/{id}"), userId, Map.of("id", id), UserDtoRead.class).getBody();
    }

    public UserDtoRead post(Integer userId, UserDtoAdd userDtoAdd) {
        return client.post(baseUrl, userId, userDtoAdd, UserDtoRead.class).getBody();
    }

    public UserDtoRead patch(Integer userId, UserDtoUpdate userDtoUpdate, Integer id) {
        return client.patch(baseUrl.concat("/{id}"), userId, Map.of("id", id), null, userDtoUpdate, UserDtoRead.class).getBody();
    }

    public void delete(Integer userId, Integer id) {
        client.delete(baseUrl.concat("/{id}"), userId, Map.of("id", id));
    }

}
