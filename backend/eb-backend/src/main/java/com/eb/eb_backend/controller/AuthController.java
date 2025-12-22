package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.service.AuthService;
import com.eb.eb_backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, 
                                   HttpServletRequest request,
                                   HttpServletResponse httpResponse) {
        try {
            LoginResponse response = authService.login(loginRequest);
            
            // Créer un cookie HTTPOnly pour le token JWT (protection XSS)
            // Gestion dynamique du HTTPS : secure = true si HTTPS, false si HTTP (localhost)
            boolean isSecure = request.isSecure();
            // En production HTTPS cross-origin, utiliser SameSite=None (obligatoire avec Secure)
            // En localhost HTTP, utiliser SameSite=Lax (même domaine ou ports différents)
            String sameSite = isSecure ? "None" : "Lax";
            
            ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", response.getToken())
                .httpOnly(true)       // Protection contre XSS - JavaScript ne peut pas accéder au cookie
                .secure(isSecure)      // Dynamique : true en HTTPS (production), false en HTTP (localhost)
                .path("/")             // Cookie disponible pour toutes les routes
                .maxAge(24 * 60 * 60)  // 24 heures
                .sameSite(sameSite)    // None pour cross-origin HTTPS, Lax pour localhost HTTP
                .build();
            
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            
            // Retourner uniquement les infos utilisateur (le token est dans le cookie HttpOnly)
            return ResponseEntity.ok(new LoginResponse(null, response.getUser()));
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.error("Login failed - User not found: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest().body("Email ou mot de passe incorrect");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.error("Login failed - Bad credentials: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest().body("Email ou mot de passe incorrect");
        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.badRequest().body("Erreur lors de la connexion: " + e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().build();
            }
            String oldToken = authHeader.substring(7);
            String username = jwtUtil.extractUsername(oldToken);
            if (username == null) {
                return ResponseEntity.badRequest().build();
            }
            String newToken = jwtUtil.generateToken(username, java.util.Map.of());
            return ResponseEntity.ok(new LoginResponse(newToken, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        try {
            boolean verified = authService.verifyEmail(email, code);
            if (verified) {
                return ResponseEntity.ok("Email vérifié avec succès");
            } else {
                return ResponseEntity.badRequest().body("Code de vérification invalide");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la vérification");
        }
    }
    
    /**
     * Route API pour vérifier le code OTP
     * POST /api/auth/verify-code
     * Body: { "email": "user@example.com", "code": "123456" }
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");
            
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email requis"));
            }
            
            if (code == null || code.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Code requis"));
            }
            
            boolean verified = authService.verifyEmailCode(email, code);
            
            if (verified) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email vérifié avec succès"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Code de vérification invalide"
                ));
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Utilisateur non trouvé"
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du code", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Erreur lors de la vérification: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse httpResponse) {
        // Supprimer le cookie JWT en le remplaçant par un cookie expiré
        boolean isSecure = request.isSecure();
        String sameSite = isSecure ? "None" : "Lax";
        
        ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", "")
            .httpOnly(true)
            .secure(isSecure)  // Dynamique : true en HTTPS, false en HTTP
            .path("/")
            .maxAge(0)           // Expire immédiatement
            .sameSite(sameSite)  // None pour cross-origin HTTPS, Lax pour localhost HTTP
            .build();
        
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        
        return ResponseEntity.ok("Déconnexion réussie");
    }
}





