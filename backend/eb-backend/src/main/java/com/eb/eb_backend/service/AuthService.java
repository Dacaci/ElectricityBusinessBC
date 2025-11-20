package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.entity.User;
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

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    
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
        
        String verificationCode = generateVerificationCode();
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);
        } catch (Exception e) {
            log.error("Failed to send verification email, activating user anyway", e);
            savedUser.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(savedUser);
        }
        
        return new UserDto(savedUser);
    }
    
    private String generateVerificationCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    public boolean verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        if (user.getStatus() == User.UserStatus.PENDING) {
            user.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(user);
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
            return true;
        }
        
        return false;
    }
}
