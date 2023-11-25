package ru.practicum.shareitgw.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgw.user.dto.UserDtoAdd;
import ru.practicum.shareitgw.user.dto.UserDtoRead;
import ru.practicum.shareitgw.user.dto.UserDtoUpdate;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserGwController {

    private final UserGwService userGwService;

    @GetMapping("")
    public List<UserDtoRead> getAllUser() {
        return userGwService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDtoRead findByIdUser(@PathVariable("id") Integer id) {
        return userGwService.getUserDto(id);
    }

    @PostMapping("")
    public UserDtoRead createUser(@RequestBody UserDtoAdd userDtoAdd) {
        return userGwService.addUser(userDtoAdd);
    }

    @PatchMapping(value = "/{userId}")
    public UserDtoRead updateUser(@PathVariable("userId") Integer id,
                                  @RequestBody UserDtoUpdate userDtoUpdate) {
        return userGwService.updateUser(userDtoUpdate, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userGwService.deleteUser(id);
    }
}
