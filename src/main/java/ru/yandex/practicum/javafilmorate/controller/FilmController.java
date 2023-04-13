package ru.yandex.practicum.javafilmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.exception.ValidationException;
import ru.yandex.practicum.javafilmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static int filmId = 1;
    private boolean isFilmValidDate;
    private LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> getFilm() {
        return new ArrayList<>(films.values());
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (!isFilmValidDate(film)) {
            log.warn("The film release date is not correct");
            throw new ValidationException("The film release date is not correct");
        } else {
            film.setId(filmId);
            films.put(filmId, film);
            filmId++;
            log.info("The film has been added", film);
        }
        return film;
    }

    @PutMapping
    public Film updated(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (films.containsKey(id) && isFilmValidDate(film)) {
            films.put(id, film);
            log.info("The film has been updated", film);
        } else if (!isFilmValidDate(film)) {
            log.warn("The film release date is not correct");
            throw new ValidationException("The film release date is not correct");
        } else {
            log.warn("This film doesn't existed");
            throw new ValidationException("This film doesn't existed");
        }
        return film;
    }

    private boolean isFilmValidDate(Film film) {
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            isFilmValidDate = false;
        } else isFilmValidDate = true;
        return isFilmValidDate;
    }

}
