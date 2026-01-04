package com.eb.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration globale pour injecter des attributs dans tous les modèles Thymeleaf
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private static final Logger log = LoggerFactory.getLogger(GlobalModelAttributes.class);

    // IMPORTANT: En production sur Render, backend.url doit être en HTTPS via BACKEND_URL
    @Value("${backend.url:https://localhost:8080}")
    private String backendUrl;

    // IMPORTANT: En production sur Render, inscription.url doit être en HTTPS via INSCRIPTION_URL
    @Value("${inscription.url:https://localhost:8082}")
    private String inscriptionUrl;

    @PostConstruct
    public void init() {
        // PRIORITÉ 1: Lire directement depuis la variable d'environnement BACKEND_URL (comme dans BackendProxyService)
        String envBackendUrl = System.getenv("BACKEND_URL");
        if (envBackendUrl != null && !envBackendUrl.isEmpty() && !envBackendUrl.equals("null")) {
            this.backendUrl = envBackendUrl.trim();
            log.info("✅ GlobalModelAttributes - backendUrl depuis BACKEND_URL (env): {}", backendUrl);
        } else {
            // PRIORITÉ 2: Propriété système backend.url (passée via -D dans Dockerfile)
            String sysBackendUrl = System.getProperty("backend.url");
            if (sysBackendUrl != null && !sysBackendUrl.isEmpty()) {
                this.backendUrl = sysBackendUrl.trim();
                log.info("✅ GlobalModelAttributes - backendUrl depuis backend.url (sys prop): {}", backendUrl);
            } else {
                // FALLBACK: Utiliser la valeur depuis @Value (application.properties)
                log.info("✅ GlobalModelAttributes - backendUrl depuis application.properties: {}", backendUrl);
            }
        }
        
        // Même logique pour inscriptionUrl
        String envInscriptionUrl = System.getenv("INSCRIPTION_URL");
        if (envInscriptionUrl != null && !envInscriptionUrl.isEmpty() && !envInscriptionUrl.equals("null")) {
            this.inscriptionUrl = envInscriptionUrl.trim();
            log.info("✅ GlobalModelAttributes - inscriptionUrl depuis INSCRIPTION_URL (env): {}", inscriptionUrl);
        } else {
            String sysInscriptionUrl = System.getProperty("inscription.url");
            if (sysInscriptionUrl != null && !sysInscriptionUrl.isEmpty()) {
                this.inscriptionUrl = sysInscriptionUrl.trim();
                log.info("✅ GlobalModelAttributes - inscriptionUrl depuis inscription.url (sys prop): {}", inscriptionUrl);
            } else {
                log.info("✅ GlobalModelAttributes - inscriptionUrl depuis application.properties: {}", inscriptionUrl);
            }
        }
        
        log.info("🔧 GlobalModelAttributes initialisé - backendUrl: {}, inscriptionUrl: {}", backendUrl, inscriptionUrl);
    }

    /**
     * Injecte l'URL du backend dans tous les modèles Thymeleaf
     * Cette variable sera disponible sous le nom "backendUrl" dans tous les templates
     */
    @ModelAttribute("backendUrl")
    public String backendUrl() {
        return backendUrl;
    }

    /**
     * Injecte l'URL du module d'inscription dans tous les modèles Thymeleaf
     * Cette variable sera disponible sous le nom "inscriptionUrl" dans tous les templates
     */
    @ModelAttribute("inscriptionUrl")
    public String inscriptionUrl() {
        return inscriptionUrl;
    }
}





















