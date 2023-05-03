package ru.yandex.practicum.javafilmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        long id = user.getId();
        if (!users.containsKey(id) || !users.containsKey(user.getEmail())) {
            user.setId(userId);
            users.put(userId, user);
            userId++;
            log.info("The user with id = {} has been added to map", user.getId());
            return user;
        } else {
            throw new UserAlreadyExistException("User with userId = " + id + "is existed");
        }
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("The user with userId = " + id + "isn't found");
        }
        log.info("Get the user by Id {}", id);
        return users.get(id);
    }


    @Override
    public List<User> getAllUsers() {
        log.debug("Get {} users", users.values().size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        long id = user.getId();
        if (users.containsKey(id)) {
            validateUserName(user);
            users.replace(id, user);
            log.info("The user with id = {} has been updated", user);
        } else {
            log.warn("This user doesn't existed");
            throw new NotFoundException("This user doesn't existed");
        }
        log.info("The user with userId = {}" + id + "has been updated");
        return user;
    }

    public void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getEmail() == null || (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now()) ||
                user.getLogin() == null)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Login ist' valid");
        }
    }
}
