package com.eb.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Configuration globale pour injecter des attributs dans tous les modèles Thymeleaf
 */
@ControllerAdvice
public class GlobalModelAttributes {

    // IMPORTANT: En production sur Render, backend.url doit être en HTTPS via BACKEND_URL
    @Value("${backend.url:https://localhost:8080}")
    private String backendUrl;

    // IMPORTANT: En production sur Render, inscription.url doit être en HTTPS via INSCRIPTION_URL
    @Value("${inscription.url:https://localhost:8082}")
    private String inscriptionUrl;

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





















