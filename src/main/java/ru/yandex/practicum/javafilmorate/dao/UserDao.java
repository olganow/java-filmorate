package ru.yandex.practicum.javafilmorate.dao;

import ru.yandex.practicum.javafilmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User createUser(User user);

    Optional getUserById(int id);

    List<User> getAllUsers();

    User updateUser(User user);

    void addFriend(int id, int friendId);

    void delete(int id, int friendId);

    List<User> commonFriends(int id, int otherId);

    List<User> allFriends(int id);

}
