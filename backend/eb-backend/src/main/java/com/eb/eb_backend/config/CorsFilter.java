package com.eb.eb_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Récupérer l'origine de la requête
        String origin = request.getHeader("Origin");
        
        // Pour les cookies HTTPOnly, on doit autoriser l'origine spécifique (pas *)
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // Fallback pour les origines non spécifiées
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");
        // IMPORTANT: Activer les credentials pour envoyer les cookies HTTPOnly
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Répondre directement aux requêtes OPTIONS (preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}

