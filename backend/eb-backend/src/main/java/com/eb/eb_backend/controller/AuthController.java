package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.service.AuthService;
import com.eb.eb_backend.security.JwtUtil;
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
                                   HttpServletResponse httpResponse) {
        try {
            LoginResponse response = authService.login(loginRequest);
            
            ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", response.getToken())
                .httpOnly(true)
                .secure(true)  // Nécessaire pour SameSite=None en HTTPS
                .sameSite("None")  // Permet l'envoi cross-origin (frontend/backend sur domaines différents)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
            
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            
            return ResponseEntity.ok(new LoginResponse(null, response.getUser()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Email ou mot de passe incorrect");
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
    public ResponseEntity<String> logout(HttpServletResponse httpResponse) {
        ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", "")
            .httpOnly(true)
            .secure(true)  // Nécessaire pour SameSite=None en HTTPS
            .sameSite("None")  // Permet l'envoi cross-origin (frontend/backend sur domaines différents)
            .path("/")
            .maxAge(0)
            .build();
        
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        
        return ResponseEntity.ok("Déconnexion réussie");
    }
}





