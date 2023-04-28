package ru.yandex.practicum.javafilmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            validateUserName(user);
            user.setId(userId);
            users.put(userId, user);
            userId++;
            log.info("The user has been added", user);
            return user;
        } else {
            throw new ValidationException("User with userId = " + id + "is existed");
        }
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new ValidationException("The user with userId = " + id + "isn't found");
        }
        log.info("Get the user by Id");
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        log.debug("Get all users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            validateUserName(user);
            users.put(id, user);
            log.info("The user has been updated", user);
        } else {
            log.warn("This user doesn't existed");
            throw new ValidationException("This user doesn't existed");
        }
        log.info("The user with userId = " + id + "has been updated");
        return user;
    }


    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
