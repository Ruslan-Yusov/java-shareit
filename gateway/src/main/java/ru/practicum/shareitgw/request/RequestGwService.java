package ru.practicum.shareitgw.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareitgw.connector.RequestConnector;
import ru.practicum.shareitgw.exeption.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
public class RequestGwService {

    @Autowired
    private RequestConnector connector;

    public static class RequestDtoReadList extends ArrayList<RequestDtoRead> {
    }

    public List<RequestDtoRead> getAllRequest(Integer userId) {
        return connector.getAllRequest(userId);
    }

    public RequestDtoRead getById(Integer id, Integer authorId) {
        return connector.getById(authorId, id);
    }

    public List<RequestDtoRead> getAllRequestPag(Integer userId, Integer from, Integer size) {
        int from1 = ofNullable(from).orElse(0);
        int size1 = ofNullable(size).orElse(10000);
        if (from1 < 0 || size1 <= 0) {
            throw new BadRequestException("invalid paging");
        }
        Map<String, Object> queryParams = new HashMap<>();

        ofNullable(from1).ifPresent(v -> queryParams.put("from", v));
        ofNullable(size1).ifPresent(v -> queryParams.put("size", v));
        return connector.getPag(userId, queryParams);
    }

    public RequestDtoRead addRequest(Integer idUser, RequestDtoAdd requestDtoAdd) {
        return connector.addRequest(idUser, requestDtoAdd);
    }
}
