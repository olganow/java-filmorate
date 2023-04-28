package ru.yandex.practicum.javafilmorate.storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.model.Film;

import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage  {

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film getFilmById(Integer id) {
        return null;
    }

    @Override
    public List<Film> getFilms() {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }
}
