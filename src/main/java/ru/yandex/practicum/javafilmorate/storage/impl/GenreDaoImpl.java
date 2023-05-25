package ru.yandex.practicum.javafilmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.storage.GenreDao;

import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createFilmGenre(Film film) {
        String sqlQueryGenres = "INSERT INTO film_genre (film_id,genre_id) VALUES (?,?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                this.isGenreExisted(genre.getId());
                jdbcTemplate.update(sqlQueryGenres, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public Genre getGenreById(int id) {
        this.isGenreExisted(id);
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public void updateFilmGenre(Film film) {
        String sqlQueryGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryGenres, film.getId());
        this.createFilmGenre(film);

    }

    @Override
    public void isGenreExisted(int id) {
        String sqlQuery = "SELECT name FROM genres WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new NotFoundException("Genre id: " + id + " does not exist...");
        }
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
