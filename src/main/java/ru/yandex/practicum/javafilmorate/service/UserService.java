package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public User getUserById(Integer id) {
        log.info("Get the user with id = ", id);
        return userStorage.getUserById(id);
    }


    public User addFriend(Integer id, Integer friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        log.info("The friend with id = ", friendId, " has been added to the user with id =", id);
        updateUser(user);
        friend.getFriends().add(id);
        log.info("The friend with id = ", id, " has been added to the user with id =", friendId);
        updateUser(friend);
        return user;
    }

    public void removeFriendById(Integer id, Integer friendId) {
        User user = getUserById(id);
        log.info("The friend with id = ", friendId, " has been removed to the user with id =", id);
        user.getFriends().remove(friendId);
    }


    public List<User> getFriendsList(Integer id) {
        User user = getUserById(id);
        log.info("Get the friends list for the user with id = ", id);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int userFriendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(userFriendId);

        if (user.getFriends() == null || friend.getFriends() == null) {
            return new ArrayList<>();
        }

        List<Integer>userFriends =  user.getFriends();
        List<Integer> friendFriends = friend.getFriends();
        userFriends.retainAll(friendFriends);
        log.info("Get common friends");
        return userFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

}