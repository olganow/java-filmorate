package ru.yandex.practicum.javafilmorate.storage;

import ru.yandex.practicum.javafilmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User getUserById(Integer id);

    List<User> getUsers();

    User updateUser(User user);
}
