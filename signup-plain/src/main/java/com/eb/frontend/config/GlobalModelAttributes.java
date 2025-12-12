package com.eb.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Configuration globale pour injecter des attributs dans tous les modèles Thymeleaf
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    /**
     * Injecte l'URL du backend dans tous les modèles Thymeleaf
     * Cette variable sera disponible sous le nom "backendUrl" dans tous les templates
     */
    @ModelAttribute("backendUrl")
    public String backendUrl() {
        return backendUrl;
    }
}







