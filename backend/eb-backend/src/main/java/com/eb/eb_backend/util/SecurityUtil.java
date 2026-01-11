package com.eb.eb_backend.util;

import com.eb.eb_backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SecurityUtil {
    
    private final JwtUtil jwtUtil;
    
    public SecurityUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Extrait l'ID de l'utilisateur depuis le token JWT de la requête
     * Cherche d'abord dans le cookie JWT_TOKEN, puis dans le header Authorization
     * @param request La requête HTTP
     * @return L'ID de l'utilisateur ou null si non trouvé
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        String token = null;
        
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }
        
        if (token != null) {
            try {
                return jwtUtil.extractUserId(token);
            } catch (Exception e) {
                // Token invalide ou expiré
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Extrait l'ID de l'utilisateur depuis le contexte de sécurité Spring
     * @return L'ID de l'utilisateur ou null si non authentifié
     */
    public Long getCurrentUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            // Pour l'instant, on ne peut pas facilement récupérer l'userId depuis UserDetails
            // On préfère utiliser getCurrentUserId(HttpServletRequest)
            return null;
        }
        return null;
    }
}




































