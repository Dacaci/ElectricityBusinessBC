package com.eb.eb_backend.scheduler;

import com.eb.eb_backend.repository.EmailVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Tâche planifiée pour nettoyer automatiquement les codes OTP expirés
 * Conforme au RGPD : suppression automatique des données après expiration
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OtpCleanupScheduler {
    
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    
    /**
     * Nettoie les codes OTP expirés toutes les heures
     * Supprime les codes dont la date d'expiration est passée
     */
    @Scheduled(fixedRate = 3600000) // Toutes les heures (3600000 ms = 1h)
    @Transactional
    public void cleanupExpiredOtpCodes() {
        Instant now = Instant.now();
        long deletedCount = emailVerificationCodeRepository.deleteByExpiresAtBefore(now);
        
        if (deletedCount > 0) {
            log.info("🧹 Nettoyage RGPD : {} codes OTP expirés supprimés", deletedCount);
        }
    }
}

