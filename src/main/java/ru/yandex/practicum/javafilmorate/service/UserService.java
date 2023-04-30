package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.info("The user has been created");
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.info("The user with id = ", user.getId(), " has been updated");
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        log.info("Get all users with id = ");
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        log.info("Get the user with id = ", id);
        return userStorage.getUserById(id);
    }


    public User addFriend(Long id, Long friendId) {
        if (id == null || friendId == null) {
            throw new NotFoundException("Negative value is not allowed");
        }
        if (getUserById(id).getFriends().contains(friendId)) {
            log.warn("Пользователи уже друзья");
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Пользователь " + id + " ужедружит с " + friendId);

        }
        User user = getUserById(id);
        User friend = getUserById(friendId);
        friend.getFriends().add(friendId);
        log.info("The friend with id = ", friendId, " has been added to the user with id =", id);
        updateUser(user);
        friend.getFriends().add(id);
        log.info("The friend with id = ", id, " has been added to the user with id =", friendId);
        updateUser(friend);
        return user;
    }

    public void removeFriendById(long id, long friendId) {
        User user = getUserById(id);
        log.info("The friend with id = ", friendId, " has been removed to the user with id =", id);
        user.getFriends().remove(friendId);
    }



    public List<User> getAllFriends(long id) {
        return getUserById(id).getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        return getUserById(userId).getFriends()
                .stream()
                .filter(getUserById(friendId).getFriends()::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }


}




