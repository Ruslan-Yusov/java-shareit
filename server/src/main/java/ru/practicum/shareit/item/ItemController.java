package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

/**
 * Item контроллер.
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

    @PostMapping("/{itemId}/comment")
    public CommentDtoRead addItemComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PathVariable("itemId") Integer itemId,
                                      @RequestBody CommentDtoAdd commentDtoAdd) {
        return itemService.addItemComment(userId, itemId, commentDtoAdd);
    }
}