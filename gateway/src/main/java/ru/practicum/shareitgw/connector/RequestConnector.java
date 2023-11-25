package ru.practicum.shareitgw.connector;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.shareitgw.request.RequestDtoAdd;
import ru.practicum.shareitgw.request.RequestDtoRead;
import ru.practicum.shareitgw.request.RequestGwService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestConnector {
    private final BaseClient client;
    @Value("${shareit-gateway.services.request}")
    private String baseUrl;

    public List<RequestDtoRead> getAllRequest(Integer userId) {
        return client.get(baseUrl, userId, RequestGwService.RequestDtoReadList.class).getBody();
    }

    public RequestDtoRead getById(Integer userId, Integer id) {
        return client.get(baseUrl.concat("/{id}"), userId, Map.of("id", id), RequestDtoRead.class).getBody();
    }

    public List<RequestDtoRead> getPag(Integer userId, Map<String, Object> queryParams) {
        return client.get(baseUrl.concat("/all"), userId, null, queryParams, RequestGwService.RequestDtoReadList.class).getBody();
    }

    public RequestDtoRead addRequest(Integer userId, RequestDtoAdd requestDtoAdd) {
        return client.post(baseUrl, userId, requestDtoAdd, RequestDtoRead.class).getBody();
    }
}
