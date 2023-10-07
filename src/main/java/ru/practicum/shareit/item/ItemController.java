package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoRead;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("")
    public List<ItemDtoRead> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItem(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoRead> search(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestParam("text") String namePart) {
        return itemService.findBySubstr(namePart);
    }

    @GetMapping("{itemId}")
    public ItemDtoRead getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                   @PathVariable("itemId") Integer itemId) {
        return itemService.getByIdItem(itemId, userId);
    }

    @PostMapping("")
    public ItemDtoRead addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @RequestBody ItemDtoAdd itemDtoAdd) {
        return itemService.addItem(itemDtoAdd, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoRead updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @PathVariable("itemId") Integer itemId,
                                  @RequestBody ItemDtoUpdate itemDtoUpdate) {
        return itemService.updateItem(itemDtoUpdate, userId, itemId);

    }
}