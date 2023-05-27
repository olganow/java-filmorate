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

import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public List<Film> getFavoritesFilms(int id) {
        String sqlQuery = "SELECT f.*, rm.name AS mpa_name FROM films AS f " +
                "LEFT JOIN rating_mpa AS rm ON f.rating_id = rm.id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, id);
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
    public void isFilmExisted(int id) {
        String sqlQuery = "SELECT id FROM films WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new NotFoundException("Film id: " + id + " does not exist...");
        }
    }

    public void loadGenres(List<Film> films) {
        final Map<Integer, Film> ids = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT * from genres g, film_genre fg where fg.genre_id = g.id AND fg.film_id in (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
        //Получили из ResultSet'a идентификатор фильма и извлекли по нему из мапы значение)
        final Film film = getFilmById(rs.getInt("FILM_ID"));
        //Добавили в коллекцию внутри объекта класса FIlm новый жанр)
        makeGenre(rs, 0);
        //Преобразуем коллекцию типа Film к Integer и в массив, так как передавать требуется именно его
        }, films.stream().map(Film::getId).toArray());
    }


    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(rs.getInt("rating_id"), rs.getString("mpa_name"));
        List<Film> films = getAllFilms();
        loadGenres(films);

        return new Film(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                mpa,
               new LinkedHashSet<>()
        );
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
