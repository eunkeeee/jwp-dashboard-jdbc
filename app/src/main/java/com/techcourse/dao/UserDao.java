package com.techcourse.dao;

import com.techcourse.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserDao(final DataSource dataSource) {
        this(new JdbcTemplate(dataSource));
    }

    public void insert(final Connection conn, final User user) {
        final var sql = "insert into users (account, password, email) values (?, ?, ?)";
        jdbcTemplate.update(conn, sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(final Connection conn, final User user) {
        final var sql = "UPDATE users SET account = ?, password = ?, email = ?  WHERE id = ?";
        jdbcTemplate.update(conn, sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll(final Connection conn) {
        final var sql = "SELECT * FROM users";
        return jdbcTemplate.query(conn, sql, User.class);
    }

    public Optional<User> findById(final Connection conn, final Long id) {
        final var sql = "select id, account, password, email from users where id = ?";
        return Optional.of(jdbcTemplate.queryForObject(conn, sql, User.class, id));
    }

    public Optional<User> findByAccount(final Connection conn, final String account) {
        final var sql = "select id, account, password, email from users where account = ?";
        return Optional.of(jdbcTemplate.queryForObject(conn, sql, User.class, account));
    }

    public void deleteAll(final Connection conn) {
        final String alterAutoIncrementSql = "TRUNCATE TABLE users RESTART IDENTITY";
        jdbcTemplate.update(conn, alterAutoIncrementSql);
    }
}
