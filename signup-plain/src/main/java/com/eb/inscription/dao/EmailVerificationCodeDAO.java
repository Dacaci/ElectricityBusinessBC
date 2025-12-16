package com.eb.inscription.dao;

import java.sql.*;
import java.time.Instant;

/**
 * DAO pour les codes de vérification email avec JDBC PUR (pas de Spring Data JPA)
 */
public class EmailVerificationCodeDAO {
    
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    
    public EmailVerificationCodeDAO(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        
        // Charger le driver JDBC PostgreSQL
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL non trouvé", e);
        }
    }
    
    /**
     * Obtenir une connexion JDBC (pas de Spring DataSource)
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    
    /**
     * Créer un code de vérification pour un utilisateur
     * @return L'ID du code créé
     */
    public long createCode(long userId, String codeHash, Instant expiresAt) throws SQLException {
        String sql = "INSERT INTO email_verification_codes(user_id, code_hash, expires_at) VALUES(?,?,?) RETURNING id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setString(2, codeHash);
            stmt.setTimestamp(3, Timestamp.from(expiresAt));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Échec de la création du code de vérification");
    }
    
    /**
     * Trouver le code actif (non utilisé, non expiré) pour un utilisateur
     */
    public EmailVerification findActiveByUser(long userId) throws SQLException {
        String sql = "SELECT id, user_id, code_hash, expires_at, attempt_count, used_at " +
                     "FROM email_verification_codes " +
                     "WHERE user_id=? AND used_at IS NULL AND expires_at > now() " +
                     "ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                
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
    
    /**
     * Incrémenter le compteur de tentatives
     */
    public void incrementAttempt(long id) throws SQLException {
        String sql = "UPDATE email_verification_codes SET attempt_count = attempt_count + 1 WHERE id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Marquer le code comme utilisé
     */
    public void markUsed(long id) throws SQLException {
        String sql = "UPDATE email_verification_codes SET used_at = now() WHERE id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Classe interne pour représenter un code de vérification
     */
    public static class EmailVerification {
        private final Long id;
        private final Long userId;
        private final String codeHash;
        private final Instant expiresAt;
        private final Integer attemptCount;
        private final Instant usedAt;
        
        public EmailVerification(Long id, Long userId, String codeHash, Instant expiresAt,
                                 Integer attemptCount, Instant usedAt) {
            this.id = id;
            this.userId = userId;
            this.codeHash = codeHash;
            this.expiresAt = expiresAt;
            this.attemptCount = attemptCount;
            this.usedAt = usedAt;
        }
        
        public Long getId() { return id; }
        public Long getUserId() { return userId; }
        public String getCodeHash() { return codeHash; }
        public Instant getExpiresAt() { return expiresAt; }
        public Integer getAttemptCount() { return attemptCount; }
        public Instant getUsedAt() { return usedAt; }
    }
}
