package ru.yandex.practicum.javafilmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.GenreDao;
import ru.yandex.practicum.javafilmorate.model.Genre;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreDao GenreStorage;

    public List<Genre> getAllGenres() {
        return GenreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return GenreStorage.getGenreById(id);
    }

    public Genre getGenreById(Integer id) {
        return GenreStorage.getGenreById(id);
    }

    public List<Genre> getGenres() {
        return GenreStorage.getAllGenres();
    }
}