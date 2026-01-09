package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.EmailVerificationCode;
import com.eb.eb_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    
    /**
     * Trouve le code de vérification actif (non utilisé, non expiré) pour un utilisateur
     */
    @Query("SELECT c FROM EmailVerificationCode c " +
           "WHERE c.user.id = :userId " +
           "AND c.usedAt IS NULL " +
           "AND c.expiresAt > :now " +
           "ORDER BY c.createdAt DESC")
    Optional<EmailVerificationCode> findActiveByUserId(@Param("userId") Long userId, @Param("now") Instant now);
    
    /**
     * Trouve le code de vérification actif par email utilisateur
     */
    @Query("SELECT c FROM EmailVerificationCode c " +
           "WHERE c.user.email = :email " +
           "AND c.usedAt IS NULL " +
           "AND c.expiresAt > :now " +
           "ORDER BY c.createdAt DESC")
    Optional<EmailVerificationCode> findActiveByUserEmail(@Param("email") String email, @Param("now") Instant now);
    
    /**
     * Incrémente le compteur de tentatives
     */
    @Modifying
    @Query("UPDATE EmailVerificationCode c SET c.attemptCount = c.attemptCount + 1 WHERE c.id = :id")
    void incrementAttemptCount(@Param("id") Long id);
    
    /**
     * Marque le code comme utilisé
     */
    @Modifying
    @Query("UPDATE EmailVerificationCode c SET c.usedAt = :usedAt WHERE c.id = :id")
    void markAsUsed(@Param("id") Long id, @Param("usedAt") Instant usedAt);
    
    /**
     * Supprime les codes OTP expirés (nettoyage RGPD automatique)
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationCode c WHERE c.expiresAt < :now")
    long deleteByExpiresAtBefore(@Param("now") Instant now);
}



















