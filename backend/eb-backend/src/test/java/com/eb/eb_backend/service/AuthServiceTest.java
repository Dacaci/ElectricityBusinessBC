package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.EmailVerificationCodeRepository;
import com.eb.eb_backend.repository.UserRepository;
import com.eb.eb_backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailVerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private AuthService authService;

    private User activeUser;
    private User pendingUser;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        hashedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setEmail("test@example.com");
        activeUser.setPasswordHash(hashedPassword);
        activeUser.setFirstName("Jean");
        activeUser.setLastName("Dupont");
        activeUser.setStatus(User.UserStatus.ACTIVE);

        pendingUser = new User();
        pendingUser.setId(2L);
        pendingUser.setEmail("pending@example.com");
        pendingUser.setPasswordHash(hashedPassword);
        pendingUser.setFirstName("Marie");
        pendingUser.setLastName("Martin");
        pendingUser.setStatus(User.UserStatus.PENDING);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", hashedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any())).thenReturn("mock-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@example.com");
        loginRequest.setPassword("password123");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void testLoginBadPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongpassword", hashedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void testLoadUserByUsernameActiveUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(activeUser));

        var userDetails = authService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername("unknown@example.com");
        });
    }

    @Test
    void testVerifyEmailCodeSuccess() {
        com.eb.eb_backend.entity.EmailVerificationCode verificationCode = 
            new com.eb.eb_backend.entity.EmailVerificationCode();
        verificationCode.setId(1L);
        verificationCode.setUser(pendingUser);
        verificationCode.setCodeHash(hashedPassword);
        verificationCode.setAttemptCount(0);
        verificationCode.setExpiresAt(java.time.Instant.now().plusSeconds(900));

        when(userRepository.findByEmail("pending@example.com")).thenReturn(Optional.of(pendingUser));
        when(verificationCodeRepository.findActiveByUserEmail(anyString(), any()))
                .thenReturn(Optional.of(verificationCode));
        when(passwordEncoder.matches("123456", hashedPassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setStatus(User.UserStatus.ACTIVE);
            return u;
        });

        boolean result = authService.verifyEmailCode("pending@example.com", "123456");

        assertTrue(result);
    }
}
