package ru.yandex.practicum.javafilmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.dao.UserDao;
import ru.yandex.practicum.javafilmorate.exception.NotFoundException;
import ru.yandex.practicum.javafilmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("login"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate());
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (name,login,email,birthday) VALUES (?,?,?,?)";        //Используйте KeyHolder для получения идентификатора записи вставки Spring JdbcTemplate
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, (java.sql.Date.valueOf(user.getBirthday())));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET " +
                "name = ?," +
                "login = ?," +
                "email = ?," +
                "birthday = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id,friend_user_id) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void delete(int id, int friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_user_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> getCommonFriends(int id, int friendId) {
        String sqlQuery = "SELECT * FROM users WHERE id IN (SELECT id FROM (SELECT friend_user_id AS id"
                + " FROM friendship WHERE user_id = ?) AS ui "
                + "INNER JOIN (SELECT friend_user_id FROM friendship WHERE user_id = ?)" +
                " AS f ON ui.id = f.friend_user_id) ORDER BY id";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id, friendId);
        List<User> commonFriends = new ArrayList<>();
        while (rs.next()) {
            commonFriends.add(new User(rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getString("email"),
                    Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()));
        }
        return commonFriends.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<User> getAllFriends(int id) {
        String sqlQuery = "SELECT * FROM users WHERE id IN (SELECT friend_user_id AS id FROM friendship" +
                " WHERE user_id = ?) ORDER BY id ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<User> friends = new ArrayList<>();
        while (rs.next()) {
            friends.add(new User(rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getString("email"),
                    Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()));
        }
        return friends;
    }

    @Override
    public void isUserExisted(int id) {
        String sqlQuery = "SELECT id FROM users WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new NotFoundException("User with id= " + id + " doesn't exist...");
        }
    }
}