package ru.yandex.practicum.javafilmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.storage.FilmDao;
import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.Genre;
import ru.yandex.practicum.javafilmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name,description,release_date,duration,rating_id) VALUES (?,?,?,?,?)";
        KeyHolder id = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, id);
        film.setId(Objects.requireNonNull(id.getKey()).intValue());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.*,  rm.name AS mpa_name FROM films AS f " +
                "LEFT JOIN rating_mpa AS rm ON f.rating_id = rm.id";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT f.*,  rm.name AS mpa_name FROM films AS f " +
                "LEFT JOIN rating_mpa AS rm ON f.rating_id = rm.id WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "rating_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public void createLike(int id, int userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }


    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }


    private List<Genre> getFilmGenres(int filmId) {
        String sqlQueryGenre = "SELECT * FROM genres WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";
        return jdbcTemplate.query(sqlQueryGenre, this::makeGenre, filmId);
    }

    @Override
    public void isFilmExisted(int id) {
        String sqlQuery = "SELECT id FROM films WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new NotFoundException("Film id: " + id + " does not exist...");
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {

        Mpa mpa = new Mpa(rs.getInt("rating_id"), rs.getString("mpa_name"));

        int filmId = rs.getInt("id");
        List<Genre> genre = getFilmGenres(filmId);

        String sqlQueryLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> likes = jdbcTemplate.queryForList(sqlQueryLikes, Integer.class, filmId);

        return new Film(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(likes),
                mpa,
                new LinkedHashSet<>(genre));
    }

}
