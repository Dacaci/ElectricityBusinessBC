package com.eb.signup.user;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;

public class UserDao {
  private final DataSource ds;
  public UserDao(DataSource ds) { this.ds = ds; }

  public Long create(String firstName, String lastName, String email, String phone, 
                     LocalDate dateOfBirth, String address, String postalCode, String city, 
                     String passwordHash) throws SQLException {
    String sql = """
      INSERT INTO users(first_name, last_name, email, phone, date_of_birth, 
                       address, postal_code, city, password_hash) 
      VALUES(?,?,?,?,?,?,?,?,?) RETURNING id
      """;
    try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
      ps.setString(1, firstName);
      ps.setString(2, lastName);
      ps.setString(3, email);
      ps.setString(4, phone);
      ps.setDate(5, dateOfBirth != null ? Date.valueOf(dateOfBirth) : null);
      ps.setString(6, address);
      ps.setString(7, postalCode);
      ps.setString(8, city);
      ps.setString(9, passwordHash);
      try (var rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
    }
  }

  // Méthode de compatibilité pour l'ancien code
  public Long create(String email, String passwordHash) throws SQLException {
    return create(null, null, email, null, null, null, null, null, passwordHash);
  }

  public User findByEmail(String email) throws SQLException {
    String sql = """
      SELECT id, first_name, last_name, email, phone, date_of_birth, 
             address, postal_code, city, password_hash, status 
      FROM users WHERE email=?
      """;
    try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
      ps.setString(1, email);
      try (var rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        return new User(
          rs.getLong("id"),
          rs.getString("first_name"),
          rs.getString("last_name"),
          rs.getString("email"),
          rs.getString("phone"),
          rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
          rs.getString("address"),
          rs.getString("postal_code"),
          rs.getString("city"),
          rs.getString("password_hash"),
          rs.getString("status")
        );
      }
    }
  }

  public void activate(long id) throws SQLException {
    try (var c = ds.getConnection();
         var ps = c.prepareStatement("UPDATE users SET status='ACTIVE' WHERE id=?")) {
      ps.setLong(1, id); ps.executeUpdate();
    }
  }
}