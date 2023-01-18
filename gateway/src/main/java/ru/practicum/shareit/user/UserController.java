package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.Created;
import ru.practicum.shareit.user.validation.Updated;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getUsers();
    }

    @Validated(Created.class)
    @PostMapping()
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.error("createUser " + userDto);
        return userClient.createUser(userDto);
    }

    @Validated(Updated.class)
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserDto userDto,
                                             @PathVariable long userId) {
        log.error("updateUser " + userDto + " " + userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.error("getUser " + userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.error("deleteUser " + userId);
        userClient.deleteUser(userId);
    }
}
