package ru.yandex.practicum.javafilmorate.storage;

import ru.yandex.practicum.javafilmorate.model.Film;


import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    Film updateFilm(Film film);
}
