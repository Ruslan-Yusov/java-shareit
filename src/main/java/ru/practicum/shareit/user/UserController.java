package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoAdd;
import ru.practicum.shareit.user.dto.UserDtoRead;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public List<UserDtoRead> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDtoRead findByIdUser(@PathVariable("id") Integer id) {
        return userService.getUserDto(id);
    }

    @PostMapping("")
    public UserDtoRead createUser(@RequestBody UserDtoAdd userDtoAdd) {
        return userService.addUser(userDtoAdd);
    }

    @PatchMapping(value = "/{userId}")
    public UserDtoRead updateUser(@PathVariable("userId") Integer id,
                                  @RequestBody UserDtoUpdate userDtoUpdate) {
        return userService.updateUser(userDtoUpdate, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }
}
