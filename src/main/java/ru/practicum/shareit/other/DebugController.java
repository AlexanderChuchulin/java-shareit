package ru.practicum.shareit.other;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restart")
public class DebugController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @DeleteMapping("")
    private void restartAllController() {
        restartUsersController();
        restartItemsController();
        restartBookingController();
        restartCommentController();
    }

    @DeleteMapping("/users")
    private void restartUsersController() {
        jdbcTemplate.execute("DROP TABLE users CASCADE;");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (user_id BIGINT NOT NULL " +
                "GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, user_name VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE);");
    }

    @DeleteMapping("/items")
    private void restartItemsController() {
        jdbcTemplate.execute("DROP TABLE items CASCADE;");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS items (item_id BIGINT NOT NULL " +
                "GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, item_name VARCHAR(255) NOT NULL, " +
                "item_description VARCHAR(5000) NOT NULL, available BOOLEAN NOT NULL, " +
                "owner_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE, " +
                "request_id BIGINT REFERENCES requests (request_id));");
    }

    @DeleteMapping("/booking")
    private void restartBookingController() {
        jdbcTemplate.execute("TRUNCATE TABLE bookings RESTART IDENTITY;");
    }

    @DeleteMapping("/comment")
    private void restartCommentController() {
        jdbcTemplate.execute("TRUNCATE TABLE comments RESTART IDENTITY;");
    }
}