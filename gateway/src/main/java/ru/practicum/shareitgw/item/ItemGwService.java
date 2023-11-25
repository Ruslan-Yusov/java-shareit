package ru.practicum.shareitgw.item;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareitgw.connector.ItemConnector;
import ru.practicum.shareitgw.exeption.BadRequestException;
import ru.practicum.shareitgw.item.dto.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Item Service
 */
@Service
public class ItemGwService {

    @Autowired
    private ItemConnector connector;

    public static class ItemDtoReadList extends ArrayList<ItemDtoRead> {
    }

    public List<ItemDtoRead> getAllItem(Integer userId) {
        return connector.getAll(userId);
    }

    public List<ItemDtoRead> findBySubstr(int user, String namePart) {
        return StringUtils.isBlank(namePart)
                ? new ArrayList<>()
                : connector.search(user, namePart);
    }

    public ItemDtoRead getByIdItem(Integer itemId, Integer userId) {
        return connector.getItemById(userId, itemId);
    }

    public ItemDtoRead addItem(ItemDtoAdd itemDtoAdd, Integer userId) {
        ofNullable(itemDtoAdd.getAvailable())
                .orElseThrow(() -> new BadRequestException("Укажите статус доступности вещи"));
        if (StringUtils.isBlank(itemDtoAdd.getName()) || StringUtils.isEmpty(itemDtoAdd.getDescription())) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        return connector.addItem(userId, itemDtoAdd);
    }

    public ItemDtoRead updateItem(ItemDtoUpdate itemDtoUpdate, Integer userId, Integer itemId) {
        return connector.updateItem(userId, itemDtoUpdate, itemId);
    }

    public CommentDtoRead addItemComment(Integer userId, Integer itemId, CommentDtoAdd commentDtoAdd) {
        return connector.addItemComment(userId, itemId, commentDtoAdd);
    }
}
