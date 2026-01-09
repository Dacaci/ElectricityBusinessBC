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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));
        
        // Créer les autorités (rôles) pour Spring Security
        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() == User.UserRole.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                user.isActive(),
                authorities
        );
    }
    
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Identifiants incorrects");
        }
        
        // Inclure le rôle dans le JWT
        String token = jwtUtil.generateToken(user.getEmail(), java.util.Map.of(
                "uid", user.getId(),
                "role", user.getRole().name()
        ));
        return new LoginResponse(token, new UserDto(user));
    }
    
    @Transactional
    public boolean verifyEmailCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));
        
        EmailVerificationCode verificationCode = verificationCodeRepository
                .findActiveByUserEmail(email, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Code invalide ou expiré"));
        
        if (verificationCode.getAttemptCount() >= 5) {
            throw new IllegalArgumentException("Trop de tentatives, demandez un nouveau code");
        }
        
        verificationCodeRepository.incrementAttemptCount(verificationCode.getId());
        
        if (!passwordEncoder.matches(code, verificationCode.getCodeHash())) {
            throw new IllegalArgumentException("Code incorrect");
        }
        
        verificationCodeRepository.markAsUsed(verificationCode.getId(), Instant.now());
        
        if (user.getStatus() == User.UserStatus.PENDING) {
            user.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(user);
            
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
            } catch (Exception e) {
                log.warn("Erreur envoi email", e);
            }
            
            return true;
        }
        
        return false;
    }
    
    @Deprecated
    public boolean verifyEmail(String email, String code) {
        return verifyEmailCode(email, code);
    }
}
