package ru.yandex.practicum.javafilmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.model.Film;

import java.util.*;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            film.setId(filmId);
            films.put(filmId, film);
            filmId++;
            log.info("The film has been added", film);
            return film;
        } else {
            throw new FilmAlreadyExistException("The film with filmId = " + id + "is existed");
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("The film with filmId = " + id + "isn't found");
        }
        log.info("Get the film by Id");
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Get all films");
        return new ArrayList<>(films.values());
    }


    @Override
    public Film updateFilm(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("The film has been updated", film);
        } else {
            log.warn("This film doesn't existed");
            throw new NotFoundException("This film doesn't existed");
        }
        log.info("The film with filmId = " + id + "has been updated");
        return film;
    }
}
