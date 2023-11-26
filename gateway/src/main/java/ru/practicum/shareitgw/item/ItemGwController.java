package ru.practicum.shareitgw.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgw.item.dto.*;

import java.util.List;

/**
 * Item контроллер.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemGwController {
    private final ItemGwService itemGwService;

    @GetMapping("")
    public List<ItemDtoRead> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemGwService.getAllItem(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoRead> search(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestParam("text") String namePart) {
        return itemGwService.findBySubstr(userId, namePart);
    }

    @GetMapping("{itemId}")
    public ItemDtoRead getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                   @PathVariable("itemId") Integer itemId) {
        return itemGwService.getByIdItem(itemId, userId);
    }

    @PostMapping("")
    public ItemDtoRead addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @RequestBody ItemDtoAdd itemDtoAdd) {
        return itemGwService.addItem(itemDtoAdd, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoRead updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @PathVariable("itemId") Integer itemId,
                                  @RequestBody ItemDtoUpdate itemDtoUpdate) {
        return itemGwService.updateItem(itemDtoUpdate, userId, itemId);

    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoRead addItemComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable("itemId") Integer itemId,
                                         @RequestBody CommentDtoAdd commentDtoAdd) {
        return itemGwService.addItemComment(userId, itemId, commentDtoAdd);
    }
}