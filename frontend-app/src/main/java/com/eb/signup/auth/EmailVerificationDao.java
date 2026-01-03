package com.eb.signup.auth;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;

public class EmailVerificationDao {
  private final DataSource ds;
  public EmailVerificationDao(DataSource ds) { this.ds = ds; }

  public long createCode(long userId, String codeHash, Instant expiresAt) throws SQLException {
    String sql = "INSERT INTO email_verification_codes(user_id, code_hash, expires_at) VALUES(?,?,?) RETURNING id";
    try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      ps.setString(2, codeHash);
      ps.setTimestamp(3, Timestamp.from(expiresAt));
      try (var rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
    }
  }

  public EmailVerification findActiveByUser(long userId) throws SQLException {
    String sql = """
      SELECT id, user_id, code_hash, expires_at, attempt_count, used_at
      FROM email_verification_codes
      WHERE user_id=? AND used_at IS NULL AND expires_at > now()
      ORDER BY created_at DESC
      LIMIT 1
      """;
    try (var c = ds.getConnection(); var ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      try (var rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        return new EmailVerification(
          rs.getLong("id"),
          rs.getLong("user_id"),
          rs.getString("code_hash"),
          rs.getTimestamp("expires_at").toInstant(),
          rs.getInt("attempt_count"),
          rs.getTimestamp("used_at") == null ? null : rs.getTimestamp("used_at").toInstant()
        );
      }
    }
  }

  public void incrementAttempt(long id) throws SQLException {
    try (var c = ds.getConnection();
         var ps = c.prepareStatement("UPDATE email_verification_codes SET attempt_count = attempt_count + 1 WHERE id=?")) {
      ps.setLong(1, id); ps.executeUpdate();
    }
  }

  public void markUsed(long id) throws SQLException {
    try (var c = ds.getConnection();
         var ps = c.prepareStatement("UPDATE email_verification_codes SET used_at = now() WHERE id=?")) {
      ps.setLong(1, id); ps.executeUpdate();
    }
  }
}
