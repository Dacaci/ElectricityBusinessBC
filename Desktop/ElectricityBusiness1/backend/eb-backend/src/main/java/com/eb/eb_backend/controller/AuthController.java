package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.service.AuthService;
import com.eb.eb_backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
}





