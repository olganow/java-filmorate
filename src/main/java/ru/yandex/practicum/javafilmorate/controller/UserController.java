package ru.yandex.practicum.javafilmorate.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.User;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;
    private boolean isUserValid = false;

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User cerateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getEmail()) || users.containsKey(user.getId())) {
            log.warn("This user is existed");
            throw new ValidationException("Thi s user is existed");
        } else {
            validateUserName(user);
            user.setId(userId);
            users.put(userId, user);
            userId++;
            log.info("The user has been added", user);
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            validateUserName(user);
            users.put(id, user);
            log.info("The user has been updated", user);
        } else {
            log.warn("This user doesn't existed");
            throw new ValidationException("This user doesn't existed");
        }
        return user;
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
