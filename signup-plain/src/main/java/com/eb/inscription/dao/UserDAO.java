package com.eb.inscription.dao;

import com.eb.inscription.model.User;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * DAO avec JDBC PUR (pas de Spring Data JPA ni Hibernate)
 * Accès direct à la base de données PostgreSQL
 */
public class UserDAO {
    
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    
    public UserDAO(String dbUrl, String dbUsername, String dbPassword) {
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
     * Vérifier si un email existe déjà
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Sauvegarder un utilisateur avec JDBC pur
     */
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword()); // Déjà hashé avec BCrypt
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, "PENDING"); // Statut initial
            stmt.setTimestamp(7, Timestamp.valueOf(user.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'insertion, aucune ligne affectée");
            }
            
            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Échec de la récupération de l'ID généré pour l'utilisateur");
                }
            }
        }
    }
    
    /**
     * Trouver un utilisateur par email
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Activer un utilisateur (après vérification)
     */
    public void enableUser(String email) throws SQLException {
        String sql = "UPDATE users SET status = 'ACTIVE' WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec cet email");
            }
        }
    }
    
    /**
     * Mapper un ResultSet vers un objet User (pas d'ORM, tout manuel)
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        // Vérifier le statut : enabled = true si status = 'ACTIVE'
        String status = rs.getString("status");
        user.setEnabled("ACTIVE".equals(status));
        // Note: verification_code n'existe plus dans la table, on utilise email_verification_codes
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
}












