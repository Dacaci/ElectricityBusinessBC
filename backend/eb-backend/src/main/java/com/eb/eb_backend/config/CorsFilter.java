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
            // Fallback pour les origines non spécifiées (en production, devrait être géré par SimpleSecurityConfig)
            // En développement local uniquement
            String requestUrl = request.getRequestURL().toString();
            if (requestUrl.contains("localhost") || requestUrl.contains("127.0.0.1")) {
                response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
            } else {
                // En production, utiliser l'origine de la requête si disponible
                String referer = request.getHeader("Referer");
                if (referer != null && !referer.isEmpty()) {
                    try {
                        java.net.URL url = new java.net.URL(referer);
                        String protocol = url.getProtocol();
                        String host = url.getHost();
                        int port = url.getPort();
                        String originFallback = port != -1 ? 
                            protocol + "://" + host + ":" + port : 
                            protocol + "://" + host;
                        response.setHeader("Access-Control-Allow-Origin", originFallback);
                    } catch (Exception e) {
                        // Si on ne peut pas parser, ne pas définir d'origine (SimpleSecurityConfig gérera)
                    }
                }
            }
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        // IMPORTANT: Avec allowCredentials=true, on ne peut pas utiliser "*" pour les headers
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers, X-Requested-With, Cookie");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Set-Cookie");
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

