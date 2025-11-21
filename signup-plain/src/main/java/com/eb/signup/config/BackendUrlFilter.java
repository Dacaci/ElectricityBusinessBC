package com.eb.signup.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "BackendUrlFilter", urlPatterns = {"/*"})
public class BackendUrlFilter implements Filter {
    
    private String backendUrl;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Récupérer BACKEND_URL depuis les variables d'environnement
        backendUrl = System.getenv("BACKEND_URL");
        if (backendUrl == null || backendUrl.isEmpty()) {
            // Fallback : utiliser localhost en développement
            backendUrl = "http://localhost:8080";
        }
        // S'assurer que l'URL ne se termine pas par /
        if (backendUrl.endsWith("/")) {
            backendUrl = backendUrl.substring(0, backendUrl.length() - 1);
        }
        System.out.println("🔧 BackendUrlFilter initialisé avec BACKEND_URL: " + backendUrl);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Ajouter BACKEND_URL comme attribut de requête pour les JSP
        httpRequest.setAttribute("BACKEND_URL", backendUrl);
        
        chain.doFilter(request, response);
    }
}

