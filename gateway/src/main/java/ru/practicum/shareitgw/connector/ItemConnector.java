package ru.practicum.shareitgw.connector;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.shareitgw.item.ItemGwService;
import ru.practicum.shareitgw.item.dto.*;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemConnector {
    private final BaseClient client;
    @Value("${shareit-gateway.services.item}")
    private String baseUrl;

    public List<ItemDtoRead> getAll(Integer userId) {
        return client.get(baseUrl, userId, ItemGwService.ItemDtoReadList.class).getBody();
    }

    public List<ItemDtoRead> search(Integer userId, String namePart) {
        return client.get(
                        baseUrl.concat("/search"),
                        userId,
                        null,
                        Map.of("text", namePart),
                        ItemGwService.ItemDtoReadList.class)
                .getBody();
    }

    public ItemDtoRead getItemById(Integer userId, Integer itemId) {
        return client.get(baseUrl.concat("/{id}"), userId, Map.of("id", itemId), ItemDtoRead.class).getBody();
    }

    public ItemDtoRead addItem(Integer userId, ItemDtoAdd itemDtoAdd) {
        return client.post(baseUrl, userId, itemDtoAdd, ItemDtoRead.class).getBody();
    }

    public ItemDtoRead updateItem(Integer userId, ItemDtoUpdate itemDtoUpdate, Integer itemId) {
        return client.patch(baseUrl.concat("/{itemId}"), userId, Map.of("itemId", itemId), null, itemDtoUpdate, ItemDtoRead.class).getBody();
    }

    public CommentDtoRead addItemComment(Integer userId, Integer itemId, CommentDtoAdd commentDtoAdd) {
        return client.post(baseUrl.concat("/{itemId}/comment"), userId, Map.of("itemId", itemId), commentDtoAdd, CommentDtoRead.class).getBody();
    }

}
