package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateUserDto;
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

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final ResendEmailService resendEmailService;
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
    
    public UserDto register(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        User user = new User();
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setPhone(createUserDto.getPhone());
        user.setDateOfBirth(createUserDto.getDateOfBirth());
        user.setAddress(createUserDto.getAddress());
        user.setPostalCode(createUserDto.getPostalCode());
        user.setCity(createUserDto.getCity());
        user.setPasswordHash(passwordEncoder.encode(createUserDto.getPassword()));
        user.setStatus(User.UserStatus.PENDING);
        
        User savedUser = userRepository.save(user);
        
        // Générer un code OTP à 6 chiffres
        String verificationCode = generateVerificationCode();
        
        // Hasher le code avec BCrypt avant de le stocker
        String codeHash = passwordEncoder.encode(verificationCode);
        
        // Créer et sauvegarder le code de vérification dans la DB (expiration 15 minutes)
        EmailVerificationCode verificationCodeEntity = EmailVerificationCode.builder()
                .user(savedUser)
                .codeHash(codeHash)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .attemptCount(0)
                .build();
        verificationCodeRepository.save(verificationCodeEntity);
        
        // Envoyer l'email via Resend
        try {
            boolean emailSent = resendEmailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);
            if (!emailSent) {
                log.warn("Failed to send verification email via Resend, but code was generated and saved");
            }
        } catch (Exception e) {
            log.error("Failed to send verification email via Resend", e);
            // On continue quand même, le code est dans la DB
        }
        
        return new UserDto(savedUser);
    }
    
    /**
     * Génère un code OTP à 6 chiffres
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
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
