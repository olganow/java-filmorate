package ru.yandex.practicum.javafilmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.storage.FilmStorage;

import java.util.List;
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
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
        log.info("A like added");
        updateFilm(film);
        return film;
    }

    public void removeLikes(int filmId, int like) {
        if (like < 0 || filmId < 0) {
            throw new ValidationException("Negative value is not allowed");
        }
        Film film = getFilmById(filmId);
        film.deleteLike(like);
        log.info("The like deleted");
        updateFilm(film);
    }

    public List<Film> favoritesFilms(int amount) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(amount)
                .collect(Collectors.toList());
    }

}