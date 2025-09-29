package main.java.com.eb.signup.user;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
  private final DataSource ds;
  public UserDao(DataSource ds) { this.ds = ds; }

  public Long create(String email, String passwordHash) throws SQLException {
    String sql = "INSERT INTO users(email, password_hash) VALUES(?,?) RETURNING id";
    try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
      ps.setString(1, email); ps.setString(2, passwordHash);
      try (var rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
    }
  }

  public User findByEmail(String email) throws SQLException {
    String sql = "SELECT id,email,password_hash,status FROM users WHERE email=?";
    try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
      ps.setString(1, email);
      try (var rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        return new User(
          rs.getLong("id"),
          rs.getString("email"),
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