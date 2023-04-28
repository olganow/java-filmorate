package ru.yandex.practicum.javafilmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        log.info("Get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Get all users by Id = ", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("The user has been added", user);
        return userService.createUser(user);
    }

    @PutMapping
    @ResponseBody
    public User updateUser(@Valid @RequestBody User user) {
        log.info("The user has been updated", user);
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("The friend with id = ", friendId, " has been added to the user with id =", id);
        return userService.addFriend(id, friendId);
    }


    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriendById(@PathVariable int id, @PathVariable int friendId) {
        log.info("The friend with id = ", friendId, " has been removed from user with id =", id);
        userService.removeFriendById(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Set<Integer> getFriendByID(@PathVariable Integer id) {
        log.debug("Get friends by the user Id");
        return userService.getFriendsList(id);
    }

}
