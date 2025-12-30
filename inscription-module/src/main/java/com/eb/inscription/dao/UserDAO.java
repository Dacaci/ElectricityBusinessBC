package com.eb.inscription.dao;

import com.eb.inscription.model.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDAO {

    private static final String DEFAULT_STATUS = "PENDING";
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;

    public UserDAO(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        loadDriver();
    }

    private void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL non trouvé", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone, " +
                     "date_of_birth, address, postal_code, city, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUserParameters(stmt, user);
            executeInsert(stmt, user);
        }
    }

    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getEmail());
        stmt.setString(2, user.getPasswordHash());
        stmt.setString(3, user.getFirstName());
        stmt.setString(4, user.getLastName());
        stmt.setString(5, user.getPhone());
        setDateOfBirth(stmt, user.getDateOfBirth());
        stmt.setString(7, user.getAddress());
        stmt.setString(8, user.getPostalCode());
        stmt.setString(9, user.getCity());
        stmt.setString(10, DEFAULT_STATUS);
        stmt.setTimestamp(11, Timestamp.valueOf(user.getCreatedAt()));
    }

    private void setDateOfBirth(PreparedStatement stmt, LocalDate dateOfBirth) throws SQLException {
        if (dateOfBirth != null) {
            stmt.setDate(6, Date.valueOf(dateOfBirth));
        } else {
            stmt.setNull(6, Types.DATE);
        }
    }

    private void executeInsert(PreparedStatement stmt, User user) throws SQLException {
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new IllegalStateException();
        }
        retrieveGeneratedId(stmt, user);
    }

    private void retrieveGeneratedId(PreparedStatement stmt, User user) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToUser(rs) : null;
            }
        }
    }

    public void enableUser(String email) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ACTIVE_STATUS);
            stmt.setString(2, email);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalStateException();
            }
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setDateOfBirth(extractDate(rs.getDate("date_of_birth")));
        user.setAddress(rs.getString("address"));
        user.setPostalCode(rs.getString("postal_code"));
        user.setCity(rs.getString("city"));
        user.setStatus(extractStatus(rs.getString("status")));
        user.setCreatedAt(extractTimestamp(rs.getTimestamp("created_at")));
        return user;
    }

    private LocalDate extractDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private String extractStatus(String status) {
        return status != null ? status : DEFAULT_STATUS;
    }

    private LocalDateTime extractTimestamp(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}



