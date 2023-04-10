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
    private int filmId = 1;
    private boolean isFilmValid = false;
    private LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
    //название не может быть пустым;
    //максимальная длина описания — 200 символов;
    //дата релиза — не раньше 28 декабря 1895 года;
    //продолжительность фильма должна быть положительной.

    @GetMapping
    public List<Film> getFilm() {
        return new ArrayList<>(films.values());
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if ( films.containsKey(film.getId())){
            log.warn("This film is existed");
            throw new ValidationException("This film is existed");
        } else if (film.getReleaseDate().isBefore(minReleaseDate)) {
            log.warn("The film release date is not correct ");
            throw new ValidationException("This film is existed");
 /*       } else if (film.getName().trim().length() == 0) {
            log.warn("The film name is empty");
            throw new ValidationException("This film is existed");*/
        } else{
            film.setId(filmId);
            films.put(filmId, film);
            filmId++;
            log.info("The film has been added",film);
        }
        return film;
    }

    @PutMapping
    public Film updated(@Valid @RequestBody Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("The film been updated", film);
        } else {
            log.warn("This film doesn't existed");
            throw new ValidationException("This film doesn't existed");
        }
        return film;
    }


}
