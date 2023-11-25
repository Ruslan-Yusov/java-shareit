package ru.practicum.shareitgw.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestGwController {

    private final RequestGwService service;

    @GetMapping("")
    public List<RequestDtoRead> getAllRequest(@RequestHeader("X-Sharer-User-Id")
                                              Integer userId) {
        return service.getAllRequest(userId);
    }

    @GetMapping("/{id}")
    public RequestDtoRead getById(@RequestHeader("X-Sharer-User-Id")
                                  Integer userId,
                                  @PathVariable("id")
                                  Integer id) {
        return service.getById(id, userId);
    }

    @GetMapping("/all")
    public List<RequestDtoRead> getPag(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(name = "from",
                                               required = false) Integer from,
                                       @RequestParam(name = "size",
                                               required = false) Integer size) {
        return service.getAllRequestPag(userId, from, size);
    }

    @PostMapping("")
    public RequestDtoRead addRequest(@RequestHeader("X-Sharer-User-Id")
                                     Integer userId,
                                     @RequestBody RequestDtoAdd requestDtoAdd) {
        return service.addRequest(userId, requestDtoAdd);
    }
}
