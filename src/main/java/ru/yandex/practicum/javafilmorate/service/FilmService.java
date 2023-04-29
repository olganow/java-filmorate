package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        log.info("Create the film");
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Update the film with id = ", film.getId());
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        log.info("Get all film");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) {
        log.info("Get the film with id = ", filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film addLikes(int filmId, int userId) {
        if (!getFilmById(filmId).getLikes().contains(userId)) {
            Film film = getFilmById(filmId);
            film.getLikes().add(userId);
            log.info("A like added");
            updateFilm(film);
            return film;
        } else {
            log.debug("Ошибка - пользователь не найден.");
            throw new UserAlreadyExistException("Ошибка - пользователь не найден.");
        }
    }

    public Film removeLikes(int filmId, int userID) {
        if (filmId < 0 || userID < 0) {
            throw new NotFoundException("Negative value is not allowed");
        }
        Film film = getFilmById(filmId);
        film.getLikes().remove(userID);
        log.info("The like deleted");
        updateFilm(film);
        return film;
    }

    public List<Film> favoritesFilms(Integer number) {
        if (number == null) {
            number = 10;
        }
        return filmStorage.getAllFilms().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(number)
                .collect(Collectors.toList());
    }
}





