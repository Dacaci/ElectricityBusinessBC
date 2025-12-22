package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.entity.EmailVerificationCode;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.EmailVerificationCodeRepository;
import com.eb.eb_backend.repository.UserRepository;
import com.eb.eb_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final EmailVerificationCodeRepository verificationCodeRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                user.isActive(),
                new ArrayList<>()
        );
    }
    
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
        
        UserDto userDto = new UserDto(user);
        String token = jwtUtil.generateToken(user.getEmail(), java.util.Map.of("uid", user.getId()));
        return new LoginResponse(token, userDto);
    }
    
    /**
     * Vérifie le code OTP et active l'utilisateur si valide
     */
    @Transactional
    public boolean verifyEmailCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        
        // Trouver le code de vérification actif
        EmailVerificationCode verificationCode = verificationCodeRepository
                .findActiveByUserEmail(email, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Aucun code de vérification valide trouvé"));
        
        // Vérifier le nombre de tentatives (max 5)
        if (verificationCode.getAttemptCount() >= 5) {
            throw new IllegalArgumentException("Trop de tentatives. Veuillez demander un nouveau code.");
        }
        
        // Incrémenter le compteur de tentatives
        verificationCodeRepository.incrementAttemptCount(verificationCode.getId());
        
        // Vérifier le code (BCrypt compare)
        if (!passwordEncoder.matches(code, verificationCode.getCodeHash())) {
            throw new IllegalArgumentException("Code de vérification invalide");
        }
        
        // Code valide : marquer comme utilisé et activer l'utilisateur
        verificationCodeRepository.markAsUsed(verificationCode.getId(), Instant.now());
        
        if (user.getStatus() == User.UserStatus.PENDING) {
            user.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(user);
            
            // Envoyer un email de bienvenue
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
            } catch (Exception e) {
                log.warn("Failed to send welcome email", e);
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Ancienne méthode de vérification (pour compatibilité)
     */
    @Deprecated
    public boolean verifyEmail(String email, String code) {
        return verifyEmailCode(email, code);
    }
}
