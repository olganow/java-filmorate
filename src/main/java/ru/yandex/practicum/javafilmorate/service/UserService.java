package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.javafilmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        inMemoryUserStorage.validateUserName(user);
        log.info("The user has been created");
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        inMemoryUserStorage.validateUserName(user);
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


    public void addFriend(Long id, Long friendId) {
        if (getUserById(id).getFriends().contains(friendId)) {
            log.info("The friend with id = ", friendId, " has been friend of the user with id =", id);
            throw new ValidationException(HttpStatus.BAD_REQUEST, "User " + id + " and the user " + friendId +
                    "have been friends yet ");
        }
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        log.info("The friend with id = ", friendId, " has been added to the user with id =", id);
        log.info("The friend with id = ", id, " has been added to the user with id =", friendId);
    }

    public void removeFriendById(long id, long friendId) {
        User user = getUserById(id);
        log.info("The friend with id = ", friendId, " has been removed to the user with id =", id);
        user.getFriends().remove(friendId);
    }

    public List<User> getAllFriends(long id) {
        log.info("Get All friends");
        return getUserById(id).getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() == null || friend.getFriends() == null) {
            return new ArrayList<>();
        } else if (user.getFriends().isEmpty() || friend.getFriends().isEmpty() ||
                (user.getFriends().isEmpty() && friend.getFriends().isEmpty())) {
            return new ArrayList<>();
        } else {
            log.info("Get common friends");
            List friends = getAllFriends(userId);
            friends.retainAll(getAllFriends(friendId));
            return friends;
        }
    }
}

