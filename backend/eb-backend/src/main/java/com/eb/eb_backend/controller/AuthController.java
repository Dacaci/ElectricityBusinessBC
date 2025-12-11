package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.service.AuthService;
import com.eb.eb_backend.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {
        try {
            LoginResponse response = authService.login(loginRequest);
            
            // Créer un cookie HTTPOnly pour le token JWT (protection XSS)
            // Utilisation de ResponseCookie pour supporter SameSite=None (cross-domain)
            ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", response.getToken())
                .httpOnly(true)       // Protection contre XSS - JavaScript ne peut pas accéder au cookie
                .secure(true)         // HTTPS uniquement (obligatoire avec SameSite=None)
                .path("/")            // Cookie disponible pour toutes les routes
                .maxAge(7200)         // 2 heures (même durée que le token)
                .sameSite("None")     // Permet les cookies cross-domain (frontend et backend sur domaines différents)
                .build();
            
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            
            // Retourner seulement les infos utilisateur (le token est dans le cookie HTTPOnly)
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
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody com.eb.eb_backend.dto.CreateUserDto createUserDto) {
        try {
            UserDto userDto = authService.register(createUserDto);
            return ResponseEntity.ok(userDto);
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
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpResponse) {
        // Supprimer le cookie JWT en le remplaçant par un cookie expiré
        ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)           // Expire immédiatement
            .sameSite("None")    // Permet les cookies cross-domain
            .build();
        
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        
        return ResponseEntity.ok("Déconnexion réussie");
    }
}





