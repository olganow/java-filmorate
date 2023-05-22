package ru.yandex.practicum.javafilmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.UserDao;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.User;


import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserDao userStorage;


    public User createUser(User user) {
        validateUserName(user);
        log.info("The user with id = {} has been created, ", user.getId());
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUserName(user);
        userStorage.isUserExisted(user.getId());
        log.info("The user with id = {} {}", user.getId(), " has been updated");
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        log.info("Get {} users ", userStorage.getAllUsers().size());
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        userStorage.isUserExisted(id);
        log.info("Get the user with id = {}", id);
        User user = userStorage.getUserById(id);
        log.info("Get the user with id = {}", id);
        return user;
    }

    public void addFriend(int id, int friendId) {
        userStorage.isUserExisted(id);
        userStorage.isUserExisted(friendId);
        User user = getUserById(id);
        if (user.getFriends().contains(friendId)) {
            log.info("The friend with id = {} {} {}", friendId, " has been friend of the user with id = {}", id);
            throw new ValidationException(HttpStatus.BAD_REQUEST, "User " + id + " and the user " + friendId +
                    "have been friends yet ");
        }
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        log.info("The friend with id = {} {} {}", friendId, " has been added to the user with id = ", id);
        log.info("The friend with id = {} {} {}", id, " has been added to the user with id = ", friendId);
    }

    public void removeFriendById(int id, int friendId) {
        User user = getUserById(id);
        log.info("The friend with id = {}{}{}", friendId, " has been removed to the user with id = ", id);
        user.getFriends().remove(friendId);
    }

    public List<User> getAllFriends(int id) {
        log.info("Get All friends");
        return getUserById(id).getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        log.info("Get common friends");
        List<User> friends = getAllFriends(userId);
        friends.retainAll(getAllFriends(friendId));
        return friends;

    }

    public void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
